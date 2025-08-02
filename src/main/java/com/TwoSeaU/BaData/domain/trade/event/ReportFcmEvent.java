package com.TwoSeaU.BaData.domain.trade.event;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class ReportFcmEvent {

    private String title;
    private String contents;
    private List<String> fcmTokens;

    public static ReportFcmEvent of(final String title, final String contents, final List<String> fcmTokens){

        return ReportFcmEvent.builder()
                .title(title)
                .contents(contents)
                .fcmTokens(fcmTokens)
                .build();
    }

}
