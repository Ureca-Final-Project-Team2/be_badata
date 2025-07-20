package com.TwoSeaU.BaData.domain.rental.repository;

import static com.TwoSeaU.BaData.domain.rental.entity.QReservation.reservation;
import static com.TwoSeaU.BaData.domain.rental.entity.QReview.review;
import static com.TwoSeaU.BaData.domain.store.entity.QStore.store;
import static com.TwoSeaU.BaData.domain.user.entity.QUser.user;

import com.TwoSeaU.BaData.domain.rental.entity.Review;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final String review_rating="reviewRating";

    public Slice<Review> getReviewSlice(final Long storeId, final Pageable pageable) {

        List<Review> content = queryFactory.selectFrom(review)
                .join(review.reservation, reservation).fetchJoin()
                .leftJoin(reservation.store, store).fetchJoin()
                .join(reservation.user, user).fetchJoin()
                .where(review.reservation.store.id.eq(storeId))
                .orderBy(reviewSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);

    }

    private OrderSpecifier<?> reviewSort(final Pageable pageable){

        if(!pageable.getSort().isEmpty()){

            for(Sort.Order order: pageable.getSort()){
                Order direction  = order.getDirection().isAscending()? Order.ASC:Order.DESC;

                switch (order.getProperty()){

                    case review_rating:
                        return new OrderSpecifier(direction, review.rating);

                }
            }
        }

        return new OrderSpecifier(Order.DESC, store.id);
    }


}
