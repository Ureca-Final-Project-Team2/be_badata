package com.TwoSeaU.BaData.domain.trade.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.TwoSeaU.BaData.domain.trade.dto.response.PostResponse;
import com.TwoSeaU.BaData.domain.trade.entity.Post;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Query getQuery(final String query) {
        if (query == null || query.isEmpty()) {
            return QueryBuilders.matchAll().build()._toQuery();
        }

        return QueryBuilders
                .multiMatch()
                .query(query)
                .fields(SEARCH_FIELDS)
                .type(TextQueryType.BestFields)
                .build()
                ._toQuery();
    }

    public CursorPageResponse<PostResponse> searchByTitleAndComment(final String userQuery, final String username, final Long cursor, final int size) {
        final User user = username == null ? null : userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder()
                .withQuery(getQuery(userQuery))
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

        final List<Long> postIds = postDocuments.stream()
                .map(hit -> hit.getContent().getId())
                .toList();

        final Map<Long, Post> postMap = postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        final Map<Long, Integer> postLikesCountMap = postLikesRepository.countByPostIds(postIds);

        final Set<Long> likedPostIds = username != null ? postLikesRepository.findLikedPostIdsByUserIdAndPostIds(user.getId(), postIds) : Set.of();

        final List<PostResponse> responseList = postDocuments.stream()
                .map(hit -> {
                    final Long postId = hit.getContent().getId();
                    final Post post = postMap.get(postId);
                    if (post == null) {
                        throw new GeneralException(TradeException.POST_NOT_FOUND);
                    }
                    return PostResponse.from(
                            post,
                            postLikesCountMap.getOrDefault(postId, 0),
                            likedPostIds.contains(postId)
                    );
                })
                .toList();

        return CursorPageResponse.of(responseList, nextCursor, hasNext);
    }
}
