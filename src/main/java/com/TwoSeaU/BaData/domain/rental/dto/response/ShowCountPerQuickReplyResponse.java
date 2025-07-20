package com.TwoSeaU.BaData.domain.rental.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowCountPerQuickReplyResponse {

    private String quickReplyName;
    private int count;

    public static ShowCountPerQuickReplyResponse of(final String quickReplyName, final int count){

        return ShowCountPerQuickReplyResponse.builder()
                .quickReplyName(quickReplyName)
                .count(count)
                .build();
    }

}
