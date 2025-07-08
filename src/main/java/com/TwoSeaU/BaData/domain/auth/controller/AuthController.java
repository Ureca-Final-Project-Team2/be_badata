package com.TwoSeaU.BaData.domain.auth.controller;


import com.TwoSeaU.BaData.domain.auth.controller.swagger.AuthApi;
import com.TwoSeaU.BaData.domain.auth.dto.response.CheckNewUserResponse;
import com.TwoSeaU.BaData.domain.auth.service.AuthService;
import com.TwoSeaU.BaData.domain.auth.dto.response.IssueTokenUserStatusResponse;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private static final String accessTokenHeader = "accessToken";
    private static final String refreshTokenHeader = "refreshToken";

    @GetMapping("/token/issue")
    public ResponseEntity<ApiResponse<CheckNewUserResponse>> getToken(@RequestParam("code") final String code,
                                                                      @RequestParam("provider") final String provider){

        // 토큰 생성
        IssueTokenUserStatusResponse issueTokenUserStatusResponse = authService.getServiceToken(code,provider);

        // 쿠키 생성
        ResponseCookie refreshTokenCookie = makeResponseCookie(
                issueTokenUserStatusResponse.getIssueServiceTokenResponse().getRefreshToken(),
                issueTokenUserStatusResponse.getIssueServiceTokenResponse()
                        .getRefreshTokenValidationTime() // 이 값이 필드에 있어야 함
        );

        return ResponseEntity.status(HttpStatus.OK)
                .header(accessTokenHeader, issueTokenUserStatusResponse.getIssueServiceTokenResponse().getAccessToken())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success(CheckNewUserResponse.from(
                        issueTokenUserStatusResponse.isNewUser())));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test(){

        return ResponseEntity.ok(ApiResponse.success("LOGIN SUCCESS"));
    }

    private ResponseCookie makeResponseCookie(String refreshToken,Long refreshTokenValidationTime){

        return ResponseCookie.from(refreshTokenHeader,refreshToken)
                .httpOnly(true)//   true 시 자바스크립트에서 쿠키 접근 불가 따라서 XSS 공격 방지
                .secure(false)//true 시 HTTPS 연결을 통해서만 전달 .
                .path("/")
                .maxAge(refreshTokenValidationTime)
                .sameSite("Lax")
                .build();
    }

}

