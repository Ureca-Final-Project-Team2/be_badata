package com.TwoSeaU.BaData.global.fcm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationRequest {

    private String title;

    private String content;

    private String fcmToken;

    public static NotificationRequest of(final String title, final String content, final String fcmToken){

        return NotificationRequest.builder()
                .title(title)
                .content(content)
                .fcmToken(fcmToken)
                .build();
    }

}
