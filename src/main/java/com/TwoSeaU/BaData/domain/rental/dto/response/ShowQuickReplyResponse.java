package com.TwoSeaU.BaData.domain.rental.dto.response;

import com.TwoSeaU.BaData.domain.rental.entity.QuickReply;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowQuickReplyResponse {

    private Long quickReplyId;
    private String quickReplyName;

    public static ShowQuickReplyResponse from(final QuickReply quickReply){

        return ShowQuickReplyResponse.builder()
                .quickReplyId(quickReply.getId())
                .quickReplyName(quickReply.getName())
                .build();
    }
}
