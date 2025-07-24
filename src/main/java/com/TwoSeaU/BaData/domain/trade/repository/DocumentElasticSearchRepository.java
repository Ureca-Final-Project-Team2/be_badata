package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.SearchHistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticSearchRepository extends ElasticsearchRepository<SearchHistoryDocument, String> {
}
