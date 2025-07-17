package com.TwoSeaU.BaData.domain.user.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.TwoSeaU.BaData.domain.user.dto.response.GetFollowsResponse;
import com.TwoSeaU.BaData.domain.user.entity.QUserLikes;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.entity.UserLikes;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserLikesQueryRepositoryImpl implements UserLikesQueryRepository{

	private final JPAQueryFactory queryFactory;

	private CursorPageResponse<GetFollowsResponse> getFollowsResponseInternal(
		final Long cursor, final int size, final Long userId, final Boolean isFollowers) {

		QUserLikes quserLikes = QUserLikes.userLikes;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(quserLikes.id.lt(cursor));
		}

		if(isFollowers) {
			where.and(quserLikes.followingUser.id.eq(userId));
		} else {
			where.and(quserLikes.followerUser.id.eq(userId));
		}

		List<UserLikes> qUserLikesList = queryFactory.selectFrom(quserLikes)
			.leftJoin(isFollowers ? quserLikes.followerUser : quserLikes.followingUser).fetchJoin()
			.where(where)
			.orderBy(quserLikes.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = qUserLikesList.size() > size;
		if(hasNext) qUserLikesList.remove(size);

		List<GetFollowsResponse> responseList = qUserLikesList.stream()
			.map(userLikes -> {
				User user = isFollowers ? userLikes.getFollowerUser() : userLikes.getFollowingUser();
				if (user == null) {
					throw new GeneralException(UserException.USER_NOT_FOUND);
				}
				return GetFollowsResponse.from(user, userLikes.getId());
			})
			.toList();

		Long nextCursor = responseList.isEmpty() ? null : qUserLikesList.get(qUserLikesList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}

	@Override
	public CursorPageResponse<GetFollowsResponse> getAllFollowersResponse(final Long cursor, final int size, final Long userId) {

		return getFollowsResponseInternal(cursor, size, userId, true);
	}

	@Override
	public CursorPageResponse<GetFollowsResponse> getAllFollowingsResponse(final Long cursor, final int size, final Long userId) {

		return getFollowsResponseInternal(cursor, size, userId, false);
	}
}
