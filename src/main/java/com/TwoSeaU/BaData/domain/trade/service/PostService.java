package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.UserPostsResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Data;
import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.entity.PostCategory;
import com.TwoSeaU.BaData.domain.trade.repository.DataRepository;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final DataRepository dataRepository;
    private final GifticonRepository gifticonRepository;

    public PostResponse dataToPostResponse(Data data) {
        return PostResponse.of(
                data.getId(),
                data.getTitle(),
                null,
                data.getPrice(),
                data.getCreatedAt(),
                data.getPostImage(),
                PostCategory.DATA
        );
    }

    public PostResponse gifticonToPostResponse(Gifticon gifticon) {
        return PostResponse.of(
                gifticon.getId(),
                gifticon.getTitle(),
                gifticon.getPartner(),
                gifticon.getPrice(),
                gifticon.getCreatedAt(),
                gifticon.getPostImage(),
                PostCategory.GIFTICON
        );
    }

    public PostsResponse findAllPosts() {
        List<Data> dataList = dataRepository.findByIsSold(false);
        List<Gifticon> gifticonList = gifticonRepository.findByIsSold(false);

        List<PostResponse> allPosts = new LinkedList<>();

        for (Data data : dataList) {
            allPosts.add(dataToPostResponse(data));
        }

        for (Gifticon gifticon : gifticonList) {
            allPosts.add(gifticonToPostResponse(gifticon));
        }

        allPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));


        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public UserPostsResponse getPostsByUserId(Long userId) {
        List<Data> dataList = dataRepository.findBySellerId(userId);
        List<Gifticon> gifticonList = gifticonRepository.findBySellerId(userId);

        List<PostResponse> soldingPosts = new LinkedList<>();
        List<PostResponse> soldedPosts = new LinkedList<>();

        for (Data data : dataList) {
            if(data.getIsSold()) {
                soldedPosts.add(dataToPostResponse(data));
            }
            else {
                soldingPosts.add(dataToPostResponse(data));
            }
        }

        for (Gifticon gifticon : gifticonList) {
            if(gifticon.getIsSold()) {
                soldedPosts.add(gifticonToPostResponse(gifticon));
            }
            else {
                soldingPosts.add(gifticonToPostResponse(gifticon));
            }
        }

        soldingPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));
        soldedPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        return UserPostsResponse.builder()
                .soldingPostsResponse(soldingPosts)
                .soldedPostsResponse(soldedPosts)
                .build();
    }

    public PostsResponse getPostsByDeadLine() {
        List<Data> dataList = dataRepository.findByDeadLineBefore(LocalDateTime.now().minusDays(2));
        List<Gifticon> gifticonList = gifticonRepository.findByDeadLineBefore(LocalDateTime.now().minusDays(2));

        List<PostResponse> allPosts = new LinkedList<>();

        for (Data data : dataList) {
            allPosts.add(dataToPostResponse(data));
        }

        for (Gifticon gifticon : gifticonList) {
            allPosts.add(gifticonToPostResponse(gifticon));
        }

        allPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }

    public PostsResponse searchPosts(String query) {
        List<Data> dataList = dataRepository.findByTitleContaining(query);
        List<Gifticon> gifticonList = gifticonRepository.findByTitleContaining(query);

        List<PostResponse> allPosts = new LinkedList<>();

        for (Data data : dataList) {
            allPosts.add(dataToPostResponse(data));
        }

        for (Gifticon gifticon : gifticonList) {
            allPosts.add(gifticonToPostResponse(gifticon));
        }

        allPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        return PostsResponse.builder()
                .postsResponse(allPosts)
                .build();
    }
}
