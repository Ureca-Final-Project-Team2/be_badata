package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.DeletePostLikesResponse;
import com.TwoSeaU.BaData.domain.trade.dto.response.SavePostLikesResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.PostLikes;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostLikesRepository postLikesRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavePostLikesResponse createLike(final Long postId, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

        if (postLikesRepository.findByUserIdAndPostId(user.getId(), postId).isPresent()) {
            throw new GeneralException(TradeException.ALREADY_LIKED_POST);
        }

        final PostLikes p = postLikesRepository.save(PostLikes.of(post, user));
        return SavePostLikesResponse.of(p.getId());
    }

    @Transactional
    public DeletePostLikesResponse deleteLike(final Long postId, final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final PostLikes postLikes = postLikesRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElseThrow(() -> new GeneralException(TradeException.NOT_LIKED_POST));

        postLikesRepository.delete(postLikes);
        return DeletePostLikesResponse.of(postLikes.getId());
    }
}
