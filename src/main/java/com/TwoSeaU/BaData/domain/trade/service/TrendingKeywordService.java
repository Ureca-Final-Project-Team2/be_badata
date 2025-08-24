package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.response.GetTrendingResponse;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendingKeywordService {
    private final ElasticsearchOperations elasticsearchOperations;
    private static final String INDEX_NAME = "keyword-index-v2";
    private static final int DEFAULT_TOP_COUNT = 10;
    private static final int MESSAGE_KEYWORD_OFFSET = 22;
    private static final int AGGREGATION_INTERVAL_HOURS = 1;
    private static final String KEYWORD_EXTRACTION_SCRIPT = "doc['message.keyword'].value.substring(" + MESSAGE_KEYWORD_OFFSET + ");";

    public GetTrendingResponse getTrendingKeyword() {
        try {
            return GetTrendingResponse.of(getRecentTop10KeywordsNotEs());
        }
        catch (GeneralException e){
            throw e;
        }
        catch (Exception e){
            log.error("실시간 검색 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new GeneralException(TradeException.REALTIME_SEARCH_FAILED);
        }
    }

    private String[] getRecentTop10KeywordsNotEs() {
        return new String[]{
                "스타벅스", "이디야", "투썸플레이스",
                "메가커피", "커피빈", "할리스",
                "파스쿠찌", "탐앤탐스", "빽다방",
                "폴바셋"};
    }
}
