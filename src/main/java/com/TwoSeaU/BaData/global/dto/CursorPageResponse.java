package com.TwoSeaU.BaData.global.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CursorPageResponse<T> {
	private List<T> item;
	private Long nextCursor;
	private Boolean hasNext;

	public static <T> CursorPageResponse<T> of(List<T> item, Long nextCursor, Boolean hasNext) {
		return CursorPageResponse.<T>builder()
			.item(item)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
