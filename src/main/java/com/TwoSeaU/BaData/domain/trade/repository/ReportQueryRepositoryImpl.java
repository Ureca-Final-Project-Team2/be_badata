package com.TwoSeaU.BaData.domain.trade.repository;

import java.util.List;

import com.TwoSeaU.BaData.domain.trade.entity.Post;
import com.TwoSeaU.BaData.domain.trade.entity.QPost;
import com.TwoSeaU.BaData.domain.trade.entity.QReport;
import com.TwoSeaU.BaData.domain.trade.entity.Report;
import com.TwoSeaU.BaData.domain.trade.enums.ReportStatus;
import com.TwoSeaU.BaData.domain.user.dto.response.GetReportResponse;
import com.TwoSeaU.BaData.global.dto.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportQueryRepositoryImpl implements ReportQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<GetReportResponse> getAllReportsByCursor(final ReportStatus reportStatus, final Long cursor, final int size, final Long userId) {
		final QReport qReport = QReport.report;
		final QPost qPost = QPost.post;
		final BooleanBuilder where = new BooleanBuilder();

		if(cursor != null) {
			where.and(qReport.id.lt(cursor));
		}

		where.and(qReport.user.id.eq(userId));

		if(reportStatus == ReportStatus.COMPLETE) {
			where.and(qReport.reportStatus.eq(ReportStatus.COMPLETE));
		} else {
			where.and(qReport.reportStatus.notIn(ReportStatus.COMPLETE));
		}

		final List<Report> fetchedList = queryFactory.selectFrom(qReport)
			.join(qReport.post, qPost).fetchJoin()
			.where(where)
			.orderBy(qReport.id.desc())
			.limit(size + 1)
			.fetch();

		final boolean hasNext = fetchedList.size() > size;
		final List<Report> qReportList = hasNext
			? fetchedList.subList(0, size) : fetchedList;

		List<GetReportResponse> responseList = qReportList.stream()
			.map(report -> {
				Post post = report.getPost();

				return GetReportResponse.from(report, post);
			})
			.toList();

		Long nextCursor = responseList.isEmpty() ? null : responseList.get(responseList.size() - 1).getId();

		return CursorPageResponse.of(responseList, nextCursor, hasNext);
	}
}
