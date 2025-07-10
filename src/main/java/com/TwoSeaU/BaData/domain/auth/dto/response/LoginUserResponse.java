package com.TwoSeaU.BaData.domain.auth.dto.response;

import com.TwoSeaU.BaData.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class LoginUserResponse {

    private String email;
    private String name;
    private String profileImageUrl;
    private boolean isNewUser;

    public static LoginUserResponse from(final User user,boolean isNewUser){

        return LoginUserResponse.builder()
                .email(user.getEmail())
                .name(user.getNickName())
                .profileImageUrl(user.getProfileImageUrl())
                .isNewUser(isNewUser)
                .build();
    }

}
