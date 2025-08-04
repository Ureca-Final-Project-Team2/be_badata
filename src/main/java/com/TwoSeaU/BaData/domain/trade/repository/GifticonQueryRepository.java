package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;

import java.util.List;
import java.util.Set;

public interface GifticonQueryRepository {
    List<Gifticon> getAllSales(String username, Set<Long> ignoredIds);
}
