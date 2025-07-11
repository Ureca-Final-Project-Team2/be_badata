package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.UserPostsResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.GifticonCategory;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.DataRepository;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonCategoryRepository;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final GifticonRepository gifticonRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final GifticonCategoryRepository gifticonCategoryRepository;

    public PostsResponse findAllPosts() {
        List<PostResponse> allPosts = postRepository.findByIsSoldOrderByCreatedAtDesc(false)
                .stream()
                .map(PostResponse::from)
                .toList();

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public UserPostsResponse getPostsByUserId(Long userId) {
        List<PostResponse> soldingPosts = postRepository.findByIsSoldAndSellerIdOrderByCreatedAtDesc(false, userId)
                .stream()
                .map(PostResponse::from)
                .toList();

        List<PostResponse> soldedPosts = postRepository.findByIsSoldAndSellerIdOrderByCreatedAtDesc(true, userId)
                .stream()
                .map(PostResponse::from)
                .toList();

        return UserPostsResponse.builder()
                .soldingPostsResponse(soldingPosts)
                .soldedPostsResponse(soldedPosts)
                .build();
    }

    public PostsResponse getPostsByDeadLine() {
        List<PostResponse> allPosts = postRepository.findByDeadLineBefore(LocalDateTime.now().minusDays(2))
                .stream()
                .map(PostResponse::from)
                .toList();

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public PostsResponse searchPosts(String query) {
        List<PostResponse> allPosts = postRepository.findByTitleContaining(query)
                .stream()
                .map(PostResponse::from)
                .toList();

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public SavePostResponse createGifticonPost(SaveGifticonPostRequest saveGifticonPostRequest, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
        GifticonCategory category = gifticonCategoryRepository.findByCategoryName(saveGifticonPostRequest.getCategory())
                .orElseThrow(() -> new GeneralException(TradeException.NOT_FOUND_GIFTICON_CATEGORY));

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

    public SavePostResponse createDataPost(SaveDataPostRequest saveDataPostRequest, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

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
}
