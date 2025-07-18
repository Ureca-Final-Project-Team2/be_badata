package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdatePostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.user.entity.SearchHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.SearchHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final GifticonRepository gifticonRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;
    private final PostLikesRepository postLikesRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ELAService elaService;

    public PostsResponse postsToPostResponse(final List<Post> posts, final UserDetails userdetails) {
        Optional<Long> optionalUserId;

        if(userdetails != null) {
            optionalUserId = userRepository.findByUsername(userdetails.getUsername()).map(User::getId);
        }
        else {
            optionalUserId = null;
        }

        List<PostResponse> allPosts = posts
                .stream()
                .map(post -> PostResponse.from(
                        post,
                        postLikesRepository.countByPostId(post.getId()),
                        userdetails == null ? false : postLikesRepository.existsByUserIdAndPostId(optionalUserId.get(), post.getId())
                ))
                .toList();

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }


    public PostsResponse findAllPosts(final UserDetails userdetails) {

        return postsToPostResponse(postRepository.findByIsSoldOrderByCreatedAtDesc(false), userdetails);
    }


    public UserPostsResponse getPostsByUserId(final Long userId, final UserDetails userdetails) {

        return UserPostsResponse.of(
                postsToPostResponse(postRepository.findByIsSoldAndSellerIdOrderByCreatedAtDesc(false, userId), userdetails),
                postsToPostResponse(postRepository.findByIsSoldAndSellerIdOrderByCreatedAtDesc(true, userId), userdetails));
    }


    public PostsResponse getPostsByDeadLine(final UserDetails userdetails) {

        return postsToPostResponse(postRepository.findByDeadLineBefore(LocalDateTime.now().minusDays(2)), userdetails);

    }


    public PostsResponse searchPosts(final String query, final UserDetails userdetails) {
        if(userdetails != null) {
            User user = userRepository.findByUsername(userdetails.getUsername())
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

            try {
                searchHistoryRepository.save(SearchHistory.of(user, query));
            } catch (Exception e) {
                log.info("검색 기록 저장에 실패: {}", e.getMessage());
            }
        }

        return postsToPostResponse(postRepository.findByTitleContaining(query), userdetails);
    }


    public SavePostResponse createGifticonPost(final SaveGifticonPostRequest saveGifticonPostRequest, final String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
        GifticonCategory category = gifticonCategoryRepository.findByCategoryName(saveGifticonPostRequest.getCategory())
                .orElseThrow(() -> new GeneralException(TradeException.NOT_FOUND_GIFTICON_CATEGORY));

        ELAResult result = elaService.analyzeImage(saveGifticonPostRequest.getFile());

        if (result.isManipulated()) {
            throw new GeneralException(TradeException.SUSPICIOUS_IMAGE_DETECTED);
        }

        Gifticon gifticon = new Gifticon(
                user,
                saveGifticonPostRequest.getTitle(),
                saveGifticonPostRequest.getComment(),
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                "no image",
                false,
                saveGifticonPostRequest.getIssueDate(),
                saveGifticonPostRequest.getCouponNumber(),
                saveGifticonPostRequest.getPartner(),
                category
        );

        Gifticon savedGifticon = gifticonRepository.save(gifticon);

        return SavePostResponse.builder()
                .postId(savedGifticon.getId())
                .build();
    }


    public SavePostResponse createDataPost(final SaveDataPostRequest saveDataPostRequest, final String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        ELAResult result = elaService.analyzeImage(saveDataPostRequest.getFile());

        if (result.isManipulated()) {
            throw new GeneralException(TradeException.SUSPICIOUS_IMAGE_DETECTED);
        }

        Data data = new Data(
                user,
                saveDataPostRequest.getTitle(),
                null,
                saveDataPostRequest.getPrice(),
                saveDataPostRequest.getDeadLine(),
                "no image",
                false,
                saveDataPostRequest.getMobileCarrier(),
                saveDataPostRequest.getCapacity()
        );

        Data savedData = dataRepository.save(data);

        return SavePostResponse.builder()
                .postId(savedData.getId())
                .build();
    }

    public GetPostDetailResponse getPost(final Long postId, final UserDetails user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        GetSellerResponse seller = GetSellerResponse.from(post.getSeller());

        int likesCount = postLikesRepository.countByPostId(postId);
        boolean isLiked = false;

        if(user != null) {
            User loginUser = userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
            isLiked = postLikesRepository.existsByUserIdAndPostId(loginUser.getId(), postId);
        }

        if (post instanceof Gifticon) {
            GetGifticonDetailResponse getGifticonDetailResponse =
                    GetGifticonDetailResponse.from((Gifticon) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getGifticonDetailResponse);
        }
        else if (post instanceof Data) {
            GetDataDetailResponse getDataDetailResponse =
                    GetDataDetailResponse.from((Data) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getDataDetailResponse);
        }
        else {
            throw new GeneralException(TradeException.POST_NOT_FOUND);
        }
    }

    public DeletePostResponse deletePost(final Long postId, final String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }

        postRepository.delete(post);

        return DeletePostResponse.of(postId);
    }

    public SavePostResponse modifyPost(final Long postId, final UpdatePostRequest updatePostRequest, final String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsSold()) {
            throw new GeneralException(TradeException.EXPIRED_POST_MODIFY);
        }

        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }

        post.updateCommentAndPrice(
                updatePostRequest.getComment(),
                updatePostRequest.getPrice()
        );

        return SavePostResponse.of(post.getId());
    }
}
