package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdatePostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.service.PostService;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PostsResponse>> getPosts(
            @RequestParam(required = false) String query, @AuthenticationPrincipal User user) {
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.success(postService.searchPosts(query, user)));
        }

        return ResponseEntity.ok().body(ApiResponse.success(postService.findAllPosts(user)));
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<ApiResponse<UserPostsResponse>> getPostsByUserId(@PathVariable Long userId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByUserId(userId, user)));
    }

    @GetMapping("/posts/deadline")
    public ResponseEntity<ApiResponse<PostsResponse>> getPostsByDeadLine(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByDeadLine(user)));
    }

    @GetMapping("{postId}/post")
    public ResponseEntity<ApiResponse<GetPostDetailResponse>> getPostDetail(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPost(postId, user)));
    }

    @PostMapping(path = "/posts/gifticon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SavePostResponse>> createGifticonPost(@Valid @ModelAttribute SaveGifticonPostRequest saveGifticonPostRequest,
                                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.createGifticonPost(saveGifticonPostRequest, user.getUsername())));
    }

    @PostMapping(path = "/posts/data")
    public ResponseEntity<ApiResponse<SavePostResponse>> createDataPost(@Valid @RequestBody SaveDataPostRequest saveDataPostRequest,
                                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.createDataPost(saveDataPostRequest, user.getUsername())));
    }

    @DeleteMapping("/{postId}/post")
    public ResponseEntity<ApiResponse<DeletePostResponse>> deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.deletePost(postId, user.getUsername())));
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<SavePostResponse>> modifyPost(@PathVariable Long postId, @Valid @RequestBody UpdatePostRequest updatePostRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.modifyPost(postId, updatePostRequest, user.getUsername())));
    }
}
