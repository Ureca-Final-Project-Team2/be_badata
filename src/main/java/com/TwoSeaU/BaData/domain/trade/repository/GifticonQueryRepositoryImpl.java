package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class GifticonQueryRepositoryImpl implements GifticonQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Gifticon> getAllSales(String username, Set<Long> ignoredIds) {
        QGifticon qgifticon = QGifticon.gifticon;
        BooleanBuilder where = new BooleanBuilder();

        where.and(qgifticon.isDeleted.isFalse());
        where.and(qgifticon.isSold.isFalse());
        where.and(qgifticon.deadLine.goe(java.time.LocalDate.now()));
        where.and(qgifticon.seller.username.ne(username));
        if (ignoredIds != null && !ignoredIds.isEmpty()) {
            where.and(qgifticon.id.notIn(ignoredIds));
        }

        return queryFactory.selectFrom(qgifticon)
                .where(where)
                .fetch();
    }
}
