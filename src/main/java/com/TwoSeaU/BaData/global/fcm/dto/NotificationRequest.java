package com.TwoSeaU.BaData.global.fcm.dto;

import java.util.List;
import java.util.Map;
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

    private List<String> fcmTokens;

    private Map<String, String> data;

    public static NotificationRequest forMultipleTokens(String title, String content, List<String> tokens, Map<String, String> data) {

        return NotificationRequest.builder()
                .title(title)
                .content(content)
                .fcmTokens(tokens)
                .data(data)
                .build();
    }

    public static NotificationRequest forSingleToken(String title, String content, String token, Map<String, String> data) {

        return NotificationRequest.builder()
                .title(title)
                .content(content)
                .fcmTokens(List.of(token))
                .data(data)
                .build();
    }



}
