package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.UserPostsResponse;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

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
}
