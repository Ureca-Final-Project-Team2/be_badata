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
	private final UserRepository userRepository;

	@Override
	public CursorPageResponse<GetFollowsResponse> getAllFollowersResponse(Long cursor, int size, Long userId) {
		QUserLikes quserLikes = QUserLikes.userLikes;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(quserLikes.id.lt(cursor));
		}

		where.and(quserLikes.followingUser.id.eq(userId));

		List<UserLikes> qUserLikesList = queryFactory.selectFrom(quserLikes)
			.where(where)
			.orderBy(quserLikes.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = qUserLikesList.size() > size;
		if(hasNext) qUserLikesList.remove(size);

		List<GetFollowsResponse> getFollowersResponseList = qUserLikesList.stream()
			.map(userLikes -> {
				User follower = userRepository.findById(userLikes.getFollowerUser().getId())
					.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
				return GetFollowsResponse.from(follower, userLikes.getId());
			})
			.toList();

		Long nextCursor = getFollowersResponseList.isEmpty() ? null : qUserLikesList.get(qUserLikesList.size() - 1).getId();

		return CursorPageResponse.of(getFollowersResponseList, nextCursor, hasNext);
	}

	@Override
	public CursorPageResponse<GetFollowsResponse> getAllFollowingsResponse(Long cursor, int size, Long userId) {
		QUserLikes quserLikes = QUserLikes.userLikes;
		BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(quserLikes.id.lt(cursor));
		}

		where.and(quserLikes.followerUser.id.eq(userId));

		List<UserLikes> qUserLikesList = queryFactory.selectFrom(quserLikes)
			.where(where)
			.orderBy(quserLikes.id.desc())
			.limit(size + 1)
			.fetch();

		boolean hasNext = qUserLikesList.size() > size;
		if(hasNext) qUserLikesList.remove(size);

		List<GetFollowsResponse> getFollowingsResponseList = qUserLikesList.stream()
			.map(userLikes -> {
				User following = userRepository.findById(userLikes.getFollowingUser().getId())
					.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
				return GetFollowsResponse.from(following, userLikes.getId());
			})
			.toList();

		Long nextCursor = getFollowingsResponseList.isEmpty() ? null : qUserLikesList.get(qUserLikesList.size() - 1).getId();

		return CursorPageResponse.of(getFollowingsResponseList, nextCursor, hasNext);
	}
}
