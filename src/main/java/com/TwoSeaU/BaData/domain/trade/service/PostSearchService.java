package com.TwoSeaU.BaData.domain.trade.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.PostDocument;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PostLikesRepository;
import com.TwoSeaU.BaData.domain.trade.repository.PostRepository;
import com.TwoSeaU.BaData.domain.user.entity.SearchHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.SearchHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

import static co.elastic.clients.elasticsearch._types.SortOrder.Desc;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSearchService {
    private final PostRepository postRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final PostLikesRepository postLikesRepository;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    private static final String INDEX_NAME = "post";
    private static final List<String> SEARCH_FIELDS = List.of("title", "comment");

    public CursorPageResponse<PostResponse> searchPosts(final String query, final String username, final Long cursor, final int size) {

        if (query != null && !query.isEmpty()) {
            log.info("event-keyword-search, {}", query);
            saveSearchHistoryIfUserExists(username, query);
        }

        return searchByTitleAndComment(query, username, cursor, size);
    }

    private void saveSearchHistoryIfUserExists(final String username, final String query) {
        if (username == null) {
            return;
        }

        try {
            final User user = userRepository.findByUsername(username).orElseThrow(() ->
                    new GeneralException(UserException.USER_NOT_FOUND));

            searchHistoryRepository.save(SearchHistory.of(user, query));
        } catch (Exception e) {
            log.info("검색 기록 저장에 실패: {}", e.getMessage());
        }
    }

    public CursorPageResponse<PostResponse> searchByTitleAndComment(final String query, final String username, final Long cursor, final int size) {
        final User user = username != null ? userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(TradeException.USER_NOT_FOUND)) : null;

        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder()
                .withQuery(QueryBuilders
                        .multiMatch()
                        .query(query)
                        .fields(SEARCH_FIELDS)
                        .type(TextQueryType.BestFields)
                        .build()
                        ._toQuery()
                )
                .withSort(
                        SortOptions.of(
                                s -> s.field(f -> f.field("id").order(Desc))
                        )
                )
                .withMaxResults(size + 1);

        if (cursor != null) {
            nativeQueryBuilder.withSearchAfter(List.of(cursor));
        }

        final SearchHits<PostDocument> searchHits = elasticsearchOperations.search(
                nativeQueryBuilder.build(),
                PostDocument.class,
                IndexCoordinates.of(INDEX_NAME)
        );

        final boolean hasNext = searchHits.getSearchHits().size() > size;

        final List<SearchHit<PostDocument>> postDocuments = hasNext ? searchHits.getSearchHits().subList(0, size) : searchHits.getSearchHits();

        final Long nextCursor = postDocuments.isEmpty() ? null : postDocuments.get(postDocuments.size() - 1).getContent().getId();

        final List<PostResponse> responseList = postDocuments.stream()
                .map(post -> PostResponse.from(
                        postRepository.findById(post.getContent().getId())
                                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND)),
                        postLikesRepository.countByPostId(post.getContent().getId()),
                        username != null && postLikesRepository.existsByUserIdAndPostId(user.getId(), post.getContent().getId())
                ))
                .toList();

        return CursorPageResponse.of(responseList, nextCursor, hasNext);
    }
}
