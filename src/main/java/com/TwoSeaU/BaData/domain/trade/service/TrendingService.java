package com.TwoSeaU.BaData.domain.trade.service;

import co.elastic.clients.elasticsearch._types.ScriptLanguage;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.entity.SearchHistoryDocument;
import com.TwoSeaU.BaData.domain.trade.repository.DocumentElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import co.elastic.clients.elasticsearch._types.Script;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendingService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final DocumentElasticSearchRepository documentElasticSearchRepository;


    public List<String> getRecentTop10Keywords() {
        NativeQuery searchQuery = testQuery();

        SearchHits<SearchHistoryDocument> searchHits = elasticsearchOperations.search(searchQuery,
                SearchHistoryDocument.class,
                IndexCoordinates.of("keyword-index-v2"));

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
        assert aggregations != null;

        List<StringTermsBucket> topTenBuckets = aggregations.aggregationsAsMap()
                .get("top_ten")
                .aggregation()
                .getAggregate()
                .sterms()
                .buckets()
                .array();

        List<String> result = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getMessage())
                .collect(Collectors.toList());

//        topTenBuckets.forEach(topTenBucket -> {
//            String keyword = String.valueOf(topTenBucket.key());
//            if (keyword != null && !keyword.isEmpty()) {
//                result.add(keyword);
//            }
//        });

        return result;
    }

    private NativeQuery getAllMessagesFromKeywordIndexQuery() {
        return new NativeQueryBuilder()
                .withQuery(QueryBuilders.matchAll().build()._toQuery())  // 전체 문서 조회
                .withSourceFilter(new FetchSourceFilter(new String[]{"message"}, null)) // message 필드만 포함
                .build();
    }

    private NativeQuery testQuery() {
        //message 필드만 포함
        SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"message"}, null);

        Query rangeQuery = QueryBuilders.range()
                .field("@timestamp")
                .gte(JsonData.of(LocalDateTime.now()
                        .truncatedTo(ChronoUnit.DAYS)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()))
                .lte(JsonData.of(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .build()
                ._toQuery();

        Aggregation agg = AggregationBuilders.terms()
                .field("message.keyword")
                .size(10)
                .build()
                ._toAggregation();

        return new NativeQueryBuilder()
                .withQuery(rangeQuery)
                .withSourceFilter(sourceFilter)
                .withAggregation("top_ten", agg)
                .build();
    }


    private NativeQuery getRecentTop10KeywordsNativeQuery() {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

        Script script = Script.of(scriptBuilder -> scriptBuilder.inline(inlineScriptBuilder ->
                inlineScriptBuilder.lang(ScriptLanguage.Painless)
                        //.source("doc['message.keyword'].value.substring(22);")
                        .source("doc['message'].value.substring(22);")
                        .params(Collections.emptyMap())
        ));

        Aggregation agg = AggregationBuilders.terms()
                //.script(script)
                .size(10)
                .build()
                ._toAggregation();

        Query boolQuery = QueryBuilders.bool()
                //.must(matchQuery, loggerQuery, rangeQuery)
                .build()
                ._toQuery();

        ScriptedField scriptedField = new ScriptedField("search_keyword", new ScriptData(
                ScriptType.INLINE,
                "painless",
                "return doc['message'].value.substring(22);",
//                "return doc['message.keyword'].value.substring(22);",
                "keyword_script",
                Collections.emptyMap()
        ));

        SourceFilter sourceFilter = new FetchSourceFilter(new String[] {"*"}, new String[] {});

        return queryBuilder.withQuery(boolQuery)
                .withSourceFilter(sourceFilter)
                .withScriptedField(scriptedField)
                .withAggregation("top_ten", agg)
                .build();
    }



    public GetTrendingResponse getTrending() {
        return GetTrendingResponse.of(getRecentTop10Keywords().toArray(new String[0]));
    }
}
