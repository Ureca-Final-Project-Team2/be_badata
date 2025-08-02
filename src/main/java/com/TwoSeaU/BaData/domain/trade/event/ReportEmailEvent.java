package com.TwoSeaU.BaData.domain.trade.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class ReportEmailEvent {

    private String title;
    private String contents;
    private String targetEmail;

    public static ReportEmailEvent of(final String title, final String contents, final String email){

        return ReportEmailEvent.builder()
                .title(title)
                .contents(contents)
                .targetEmail(email)
                .build();
    }
}
