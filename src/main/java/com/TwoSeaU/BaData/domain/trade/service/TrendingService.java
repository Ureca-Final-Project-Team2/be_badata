package com.TwoSeaU.BaData.domain.trade.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.ScriptLanguage;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.entity.SearchHistoryDocument;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendingService {
    private final ElasticsearchOperations elasticsearchOperations;
    private static final String INDEX_NAME = "keyword-index-v2";
    private static final int DEFAULT_TOP_COUNT = 10;
    private static final int MESSAGE_KEYWORD_OFFSET = 22;
    private static final int AGGREGATION_INTERVAL_HOURS = 6;
    private static final String KEYWORD_EXTRACTION_SCRIPT = "doc['message.keyword'].value.substring(" + MESSAGE_KEYWORD_OFFSET + ");";

    public GetTrendingResponse getTrendingKeyword() {
        try {
            return GetTrendingResponse.of(getRecentTop10Keywords().toArray(String[]::new));
        }
        catch (GeneralException e){
            throw e;
        }
        catch (Exception e){
            log.error("실시간 검색 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new GeneralException(TradeException.REALTIME_SEARCH_FAILED);
        }
    }

    public List<String> getRecentTop10Keywords() {
        NativeQuery searchQuery = getRecentTop10KeywordsNativeQuery();

        SearchHits<SearchHistoryDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                SearchHistoryDocument.class,
                IndexCoordinates.of(INDEX_NAME));

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();

        if (aggregations == null) {
            throw new GeneralException(TradeException.REALTIME_SEARCH_CONTENT_NOT_FOUND);
        }

        List<StringTermsBucket> topTenBuckets = aggregations.aggregationsAsMap()
                .get("top_ten")
                .aggregation()
                .getAggregate()
                .sterms()
                .buckets()
                .array();

        List<String> result = new LinkedList<>();

        topTenBuckets.forEach(topTenBucket -> {
                    String keyword = topTenBucket.key().stringValue();
                    if (keyword != null && !keyword.isEmpty()) {
                        result.add(keyword);
                    }
        });

        return result;
    }

    private Query createTimeRangeQuery() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(AGGREGATION_INTERVAL_HOURS);

        return QueryBuilders.range()
                .field("@timestamp")
                .gte(JsonData.of(oneHourAgo
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()))
                .lte(JsonData.of(now
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()))
                .build()
                ._toQuery();
    }

    private NativeQuery getRecentTop10KeywordsNativeQuery() {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

        Script script = Script.of(scriptBuilder -> scriptBuilder.inline(inlineScriptBuilder ->
                inlineScriptBuilder.lang(ScriptLanguage.Painless)
                        .source(KEYWORD_EXTRACTION_SCRIPT)
                        .params(Collections.emptyMap())
        ));

        Aggregation agg = AggregationBuilders.terms()
                .script(script)
                .size(DEFAULT_TOP_COUNT)
                .build()
                ._toAggregation();

        Query boolQuery = QueryBuilders.bool()
                .must(createTimeRangeQuery())
                .build()
                ._toQuery();

        ScriptedField scriptedField = new ScriptedField(INDEX_NAME, new ScriptData(
                ScriptType.INLINE,
                "painless",
                "return "+KEYWORD_EXTRACTION_SCRIPT,
                "keyword_script",
                Collections.emptyMap()
        ));

        return queryBuilder.withQuery(boolQuery)
                .withScriptedField(scriptedField)
                .withAggregation("top_ten", agg)
                .build();
    }
}
