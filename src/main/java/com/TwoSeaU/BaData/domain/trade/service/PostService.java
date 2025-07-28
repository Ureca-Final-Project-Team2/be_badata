package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.OCRResult;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.enums.PaymentStatus;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.*;
import com.TwoSeaU.BaData.domain.user.entity.SearchHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.SearchHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private final PaymentRepository paymentRepository;
    private final ELAService elaService;
    private final S3ImageService s3ImageService;
    private final OCRService ocrService;

    public PostsResponse postsToPostResponse(final List<Post> posts, final String username) {
        Optional<Long> optionalUserId;

        if(username != null) {
            optionalUserId = userRepository.findByUsername(username).map(User::getId);
        }
        else {
            optionalUserId = Optional.empty();
        }

        final List<PostResponse> allPosts = posts
                .stream()
                .map(post -> PostResponse.from(
                        post,
                        postLikesRepository.countByPostId(post.getId()),
                        username != null && postLikesRepository.existsByUserIdAndPostId(optionalUserId.get(), post.getId())
                ))
                .toList();

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public GetImageUploadResponse E3Test(final MultipartFile file){
        ELAResult elaResult = elaService.analyzeImage(file);
        OCRResult ocrResult = ocrService.extractTextFromImageFile(file);

        return GetImageUploadResponse.from(elaResult, ocrResult);
    }

    public PostsResponse findAllPosts(final String username) {

        return postsToPostResponse(postRepository.findByIsSoldAndIsDeletedOrderByCreatedAtDesc(false, false), username);
    }


    public UserPostsResponse getPostsByUserId(final Long userId, final String username) {

        return UserPostsResponse.of(
                postsToPostResponse(postRepository.findByIsSoldAndSellerIdAndIsDeletedOrderByCreatedAtDesc(false, userId, false), username),
                postsToPostResponse(postRepository.findByIsSoldAndSellerIdAndIsDeletedOrderByCreatedAtDesc(true, userId, false), username));
    }


    public PostsResponse getPostsByDeadLine(final String username) {

        return postsToPostResponse(postRepository.findByDeadLineBetweenAndIsDeleted(LocalDate.now(), LocalDate.now().plusDays(2), false), username);

    }


    public PostsResponse searchPosts(final String query, final String username) {
        log.info("event-keyword-search, {}", query);

        if(username != null) {
            final User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

            try {
                searchHistoryRepository.save(SearchHistory.of(user, query));
            } catch (Exception e) {
                log.info("검색 기록 저장에 실패: {}", e.getMessage());
            }
        }

        return postsToPostResponse(postRepository.findByIsDeletedAndTitleContaining(false, query), username);
    }


    public SavePostResponse createGifticonPost(final SaveGifticonPostRequest saveGifticonPostRequest, final String username) {

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
        final GifticonCategory category = gifticonCategoryRepository.findByCategoryName(saveGifticonPostRequest.getCategory())
                .orElseThrow(() -> new GeneralException(TradeException.NOT_FOUND_GIFTICON_CATEGORY));

        final ELAResult result = elaService.analyzeImage(saveGifticonPostRequest.getFile());

        if (result.isManipulated()) {
            throw new GeneralException(TradeException.SUSPICIOUS_IMAGE_DETECTED);
        }

        final String imageUrl = s3ImageService.saveImage(saveGifticonPostRequest.getFile(), "trades/gifticon/", saveGifticonPostRequest.getFile().getOriginalFilename());

        final Gifticon gifticon = new Gifticon(
                user,
                saveGifticonPostRequest.getTitle(),
                saveGifticonPostRequest.getComment(),
                saveGifticonPostRequest.getPrice(),
                saveGifticonPostRequest.getDeadLine(),
                imageUrl,
                false,
                saveGifticonPostRequest.getCouponNumber(),
                saveGifticonPostRequest.getPartner(),
                category
        );

        final Gifticon savedGifticon = gifticonRepository.save(gifticon);

        return SavePostResponse.builder()
                .postId(savedGifticon.getId())
                .build();
    }


    public SavePostResponse createDataPost(final SaveDataPostRequest saveDataPostRequest, final String username) {

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Data data = new Data(
                user,
                saveDataPostRequest.getTitle(),
                saveDataPostRequest.getComment(),
                saveDataPostRequest.getPrice(),
                saveDataPostRequest.getDeadLine(),
                null,
                false,
                saveDataPostRequest.getMobileCarrier(),
                saveDataPostRequest.getCapacity()
        );

        final Data savedData = dataRepository.save(data);

        return SavePostResponse.builder()
                .postId(savedData.getId())
                .build();
    }

    public GetPostDetailResponse getPost(final Long postId, final String username) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold() && username != null) {
            final User loginUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

            if (!post.getSeller().getId().equals(loginUser.getId()) && paymentRepository.findByUserIdAndPostIdAndPaymentStatus(loginUser.getId(), postId, PaymentStatus.PAID).isEmpty()) {
                throw new GeneralException(TradeException.POST_ACCESS_DENIED);
            }
        }

        final GetSellerResponse seller = GetSellerResponse.from(post.getSeller());

        final int likesCount = postLikesRepository.countByPostId(postId);
        boolean isLiked = false;

        if(username != null) {
            final User loginUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
            isLiked = postLikesRepository.existsByUserIdAndPostId(loginUser.getId(), postId);
        }

        if (post instanceof Gifticon) {
            final GetGifticonDetailResponse getGifticonDetailResponse =
                    GetGifticonDetailResponse.from((Gifticon) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getGifticonDetailResponse);
        }
        else if (post instanceof Data) {
            final GetDataDetailResponse getDataDetailResponse =
                    GetDataDetailResponse.from((Data) post, likesCount, isLiked);
            return GetPostDetailResponse.of(seller, getDataDetailResponse);
        }
        else {
            throw new GeneralException(TradeException.POST_NOT_FOUND);
        }
    }

    public DeletePostResponse deletePost(final Long postId, final String username) {
        final User user = getUserByUsername(username);
        final Post post = validateAndGetPost(postId, username);

        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }

        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if(post.getIsSold()) {
            throw new GeneralException(TradeException.SOLD_POST_DELETE_DENIED);
        }

        post.updateIsDeleted();

        return DeletePostResponse.of(postId);
    }

    public SavePostResponse modifyPostData(final Long postId, final UpdateDataPostRequest updateDataPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        post.updateCommentAndPriceAndTitle(
                updateDataPostRequest.getComment(),
                updateDataPostRequest.getPrice(),
                updateDataPostRequest.getTitle()
        );

        return SavePostResponse.of(post.getId());
    }

    public SavePostResponse modifyPostGifticon(final Long postId, final UpdateGifticonPostRequest updateGifticonPostRequest, final String username) {
        final Post post = validateAndGetPost(postId, username);

        post.updateCommentAndPrice(
                updateGifticonPostRequest.getComment(),
                updateGifticonPostRequest.getPrice()
        );

        return SavePostResponse.of(post.getId());
    }

    private Post validateAndGetPost(final Long postId, final String username) {
        final User user = getUserByUsername(username);
        final Post post = getPostById(postId);

        validatePostStatus(post);
        validatePostOwnership(post, user);

        return post;
    }

    private User getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
    }

    private Post getPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));
    }

    private void validatePostStatus(final Post post) {
        if (post.getIsDeleted()) {
            throw new GeneralException(TradeException.DELETED_POST_ACCESS_DENIED);
        }

        if (post.getIsSold()) {
            throw new GeneralException(TradeException.EXPIRED_POST_MODIFY);
        }
    }

    private void validatePostOwnership(final Post post, final User user) {
        if (!post.getSeller().getId().equals(user.getId())) {
            throw new GeneralException(TradeException.POST_ACCESS_DENIED);
        }
    }
}
