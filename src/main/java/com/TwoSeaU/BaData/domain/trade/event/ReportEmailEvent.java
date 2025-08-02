package com.TwoSeaU.BaData.domain.trade.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class ReportEmailEvent {

    private String title;
    private String content;
    private String targetEmail;

    public static ReportEmailEvent of(final String title, final String content, final String email){

        return ReportEmailEvent.builder()
                .title(title)
                .content(content)
                .targetEmail(email)
                .build();
    }
}
