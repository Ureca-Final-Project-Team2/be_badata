package com.TwoSeaU.BaData.domain.trade.repository;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.RecommendServiceFloat;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcRepository {
    private final GifticonRepository gifticonRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final int LIMIT = 10;

    public List<RecommendServiceFloat.RecommendationResult> findSimilarPosts(final float[] userVector, final int alreadyViewCount) {
        final String sql = """
                        SELECT id, vector_pg <=> ?::vector AS similarity 
                        FROM post_vector
                        ORDER BY vector_pg <=> ?::vector
                        limit ?
                                            ;
                    """;

        try (Connection conn = dataSource.getConnection()) {
            return jdbcTemplate.query(
                    sql,
                    ps -> {
                        ps.setObject(1, toPgvectorString(userVector));
                        ps.setObject(2, toPgvectorString(userVector));
                        ps.setInt(3, LIMIT + alreadyViewCount);
                    },
                    (rs, rowNum) -> {
                        Long postId = rs.getLong("id");
                        System.out.println("Post ID: " + postId);
                        Gifticon gifticon = gifticonRepository.findById(postId)
                                .orElseThrow(() -> new GeneralException(TradeException.POST_NOT_FOUND));

                        return new RecommendServiceFloat.RecommendationResult(
                                gifticon,
                                rs.getDouble("similarity"),
                                rs.getDouble("similarity")
                        );
                    }
            );

        } catch (SQLException e) {
            throw new RuntimeException("추천 게시글 검색 실패", e);
        }
    }

    public void insertPostVector(final Long postId, final float[] vector) {
        final String sql = "INSERT INTO post_vector (id, vector_pg) VALUES (?, ?::vector)";

        try (Connection conn = dataSource.getConnection()) {
            jdbcTemplate.update(sql, postId, toPgvectorString(vector));
        } catch (SQLException e) {
            throw new RuntimeException("Post vector insert failed", e);
        }
    }

    public void updatePostVector(final Long postId, final float[] vector) {
        final String sql = "UPDATE post_vector SET vector_pg = ?::vector WHERE id = ?";

        try (Connection conn = dataSource.getConnection()) {
            jdbcTemplate.update(sql, toPgvectorString(vector), postId);
        } catch (SQLException e) {
            throw new RuntimeException("Post vector update failed", e);
        }
    }


    private String toPgvectorString(float[] vector) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            if (i < vector.length - 1) sb.append(", ");
        }
        sb.append("]");

        return sb.toString();
    }
}