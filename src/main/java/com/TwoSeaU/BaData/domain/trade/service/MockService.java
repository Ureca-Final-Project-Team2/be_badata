package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostsResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockService {
    private final PostRepository postRepository;
    private final PostLikesRepository postLikesRepository;

    public PostsResponse getHotPosts() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Post> postList = postRepository.findAll(pageRequest).getContent();

        List<PostResponse> postResponses = postList.stream()
                .map(post -> PostResponse.from(post, postLikesRepository.countByPostId(post.getId()), false))
                .toList();

        return PostsResponse.of(postResponses);
    }

    public GetTrendingResponse getTrendingSearch() {
        return GetTrendingResponse.of(new String[]{
                "스타벅스",
                "배달의 민족",
                "CU",
                "편의점",
                "GS25",

                "급처",
                "파리바게뜨",
                "카카오페이지",
                "아이스 아메리카노",
                "10GB"
        });
    }
}
