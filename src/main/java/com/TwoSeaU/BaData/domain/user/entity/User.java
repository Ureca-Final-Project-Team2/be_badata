package com.TwoSeaU.BaData.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String email;

    private String profileImageUrl;

    public static User of(final String nickName,
                          final String userName,
                          final String password,
                          final Role role,
                          final SocialType socialType,
                          final String email,
                          final String profileImageUrl){

        return User.builder()
                .nickName(nickName)
                .username(userName)
                .password(password)
                .role(role)
                .socialType(socialType)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();

    }

}
