package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;
import java.util.Optional;

import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.*;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import org.springframework.stereotype.Repository;

import com.TwoSeaU.BaData.domain.trade.enums.PostCategory;
import com.TwoSeaU.BaData.domain.user.dto.response.GetSaleResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final PostLikesRepository postLikesRepository;
	private final UserRepository userRepository;

	@Override
	public CursorPageResponse<GetSaleResponse> getAllSalesByCursor(PostCategory postCategory, Boolean isSold, Long cursor, int size, Long userId) {
		QPost qpost = QPost.post;
		BooleanBuilder where = new BooleanBuilder();

		where.and(qpost.isDeleted.isFalse());

		if(postCategory != null) {
			if(postCategory == PostCategory.GIFTICON) {
				where.and(qpost.instanceOf(Gifticon.class));
			} else if(postCategory == PostCategory.DATA) {
				where.and(qpost.instanceOf(Data.class));
			}
		}

		if(isSold != null) {
			where.and(qpost.isSold.eq(isSold));
		}

		if(cursor != null) {
			where.and(qpost.id.lt(cursor));
		}

		where.and(qpost.seller.id.eq(userId));

		List<Post> posts = queryFactory.selectFrom(qpost)
			.where(where)
			.orderBy(qpost.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = posts.size() > size;
		if(hasNext) posts.remove(size);

		List<GetSaleResponse> getSaleResponseList = posts.stream()
			.map(post -> {
				int likesCount = postLikesRepository.countByPostId(post.getId());
				return GetSaleResponse.from(post, likesCount);
			})
			.toList();

		Long nextCursor = getSaleResponseList.isEmpty() ? null : posts.get(posts.size() - 1).getId();

		return CursorPageResponse.of(getSaleResponseList, nextCursor, hasNext);
	}

	@Override
	public CursorPageResponse<PostResponse> searchPostsByKeyword(final String keyword, final String username, final Long cursor, final int size) {
		QPost qpost = QPost.post;
		Optional<User> user = username == null ? Optional.empty() : Optional.ofNullable(userRepository.findByUsername(username)
				.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND)));
		BooleanBuilder where = new BooleanBuilder();

		where.and(qpost.isDeleted.isFalse());
		where.and(qpost.isSold.isFalse());

		if(keyword != null && !keyword.isEmpty()) {
			where.and(qpost.title.containsIgnoreCase(keyword)
					.or(qpost.comment.containsIgnoreCase(keyword)));
		}

		if(cursor != null) {
			where.and(qpost.id.lt(cursor));
		}

		final List<Post> postList = queryFactory.selectFrom(qpost)
				.where(where)
				.orderBy(qpost.createdAt.desc())
				.limit(size + 1)
				.fetch();

		final boolean hasNext = postList.size() > size;
		final List<Post> qPostList = hasNext
				? postList.subList(0, size) : postList;

		final List<PostResponse> responseList = qPostList.stream()
				.map(post -> PostResponse.from(
						post,
						postLikesRepository.countByPostId(post.getId()),
						username != null && postLikesRepository.existsByUserIdAndPostId(user.get().getId(), post.getId())
				))
				.toList();

		final Long nextCursor = responseList.isEmpty() ? null : postList.get(postList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}


}
