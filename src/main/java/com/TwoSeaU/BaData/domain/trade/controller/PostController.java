package com.TwoSeaU.BaData.domain.trade.controller;

import com.TwoSeaU.BaData.domain.trade.dto.request.SaveDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.SaveGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateDataPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.request.UpdateGifticonPostRequest;
import com.TwoSeaU.BaData.domain.trade.dto.response.*;
import com.TwoSeaU.BaData.domain.trade.service.PostService;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<CursorPageResponse<PostResponse>>> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok().body(ApiResponse.success(postService.searchPosts(query, user == null ? null : user.getUsername(), cursor, size)));
    }

    @GetMapping("/posts/{userId}/{isSold}")
    public ResponseEntity<ApiResponse<CursorPageResponse<PostResponse>>> getPostsByUserId(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long userId,
            @PathVariable Boolean isSold,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByUserId(userId, isSold, user == null ? null : user.getUsername(), cursor, size)));
    }

    @GetMapping("/posts/deadline")
    public ResponseEntity<ApiResponse<CursorPageResponse<PostResponse>>> getPostsByDeadLine(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPostsByDeadLine(user == null ? null : user.getUsername(), cursor, size)));
    }

    @GetMapping("{postId}/post")
    public ResponseEntity<ApiResponse<GetPostDetailResponse>> getPostDetail(@PathVariable Long postId, @AuthenticationPrincipal User user, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.getPost(postId, user == null ? null : user.getUsername(), request, response)));
    }

    @PostMapping(path = "/posts/gifticon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SavePostResponse>> createGifticonPost(@Valid @ModelAttribute SaveGifticonPostRequest saveGifticonPostRequest,
                                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.createGifticonPost(saveGifticonPostRequest, user.getUsername())));
    }

    @PostMapping(path = "/posts/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GetImageUploadResponse>> postImage(@ModelAttribute MultipartFile file) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.analyzeImage(file)));
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

    @PatchMapping("/posts/gifticon/{postId}")
    public ResponseEntity<ApiResponse<SavePostResponse>> modifyPostGifticon(@PathVariable Long postId, @Valid @RequestBody UpdateGifticonPostRequest updateGifticonPostRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.modifyPostGifticon(postId, updateGifticonPostRequest, user.getUsername())));
    }

    @PatchMapping("/posts/data/{postId}")
    public ResponseEntity<ApiResponse<SavePostResponse>> modifyPostData(@PathVariable Long postId, @Valid @RequestBody UpdateDataPostRequest updateDataPostRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(ApiResponse.success(postService.modifyPostData(postId, updateDataPostRequest, user.getUsername())));
    }
}
