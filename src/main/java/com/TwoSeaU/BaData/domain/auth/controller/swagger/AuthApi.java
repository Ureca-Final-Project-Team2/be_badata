package com.TwoSeaU.BaData.domain.auth.controller.swagger;

import com.TwoSeaU.BaData.domain.auth.dto.response.LoginUserResponse;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 관련 API")
public interface AuthApi {
    @Operation(
            summary = "토큰 발급 API",
            description = "소셜 로그인 code와 provider를 이용해 Access/Refresh Token을 발급합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "토큰 발급 성공",
                            headers = {
                                    @Header(
                                            name = "accessToken",
                                            description = "발급된 Access Token (Bearer {token})",
                                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "발급된 Refresh Token",
                                            schema = @Schema(type = "string", example = "refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "정상 응답 예시",
                                            value = """
                                                    {
                                                      "code": 20000,
                                                      "message": null,
                                                      "content": {
                                                        "email": "dionisos198@gmail.com",
                                                        "name": "이진우",
                                                        "profileImageUrl": "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
                                                        "newUser": true
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "소셜 타입이 지원되지 않을 때",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "소셜 타입이 kakao가 아닐 때",
                                            value = """
                                                    {
                                                         "code": 6000,
                                                         "message": "지원하지 않는 소셜 타입입니다",
                                                         "content": null
                                                     }
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/token/issue")
    ResponseEntity<ApiResponse<LoginUserResponse>> getServiceToken(
            @Parameter(description = "소셜 로그인 후 받은 code") @RequestParam("code") String code,
            @Parameter(description = "소셜 로그인 제공자 (예: kakao)") @RequestParam("provider") String provider
    );

    @Operation(
            summary = "토큰 재발급 API",
            description = "만료된 AccessToken과 쿠키에 담긴 RefreshToken을 기반으로 새로운 토큰을 재발급합니다.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "기존 AccessToken (Bearer {token}) -> SWAGGER 상에서 Authorize하시고 이 부분은 아무거나 적어주셔도 됩니다!",
                            in = ParameterIn.HEADER,
                            required = true,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    ),
                    @Parameter(
                            name = "refreshToken",
                            description = "HttpOnly 쿠키에 담긴 RefreshToken -> SWAGGER 상 쿠키로 저장되므로 이 부분도 아무거나 적어주셔도 됩니다",
                            in = ParameterIn.COOKIE,
                            required = true,
                            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "토큰 재발급 성공",
                            headers = {
                                    @Header(
                                            name = "accessToken",
                                            description = "새롭게 발급된 Access Token (Bearer {token})",
                                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "RefreshToken이 포함된 Set-Cookie 헤더 (HttpOnly, Secure)",
                                            schema = @Schema(type = "string", example = "refreshToken=eyJhbGciOi...; Max-Age=1209600; Path=/; HttpOnly; Secure; SameSite=None")
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "정상 응답 예시",
                                            value = """
                            {
                                "code": 20000,
                                "message": null,
                                "content": null
                            }
                            """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "RefreshToken이 유효하지 않을 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "인증 실패 예시",
                                            value = """
                                                    {
                                                      "code": 6003,
                                                      "message": "리프레시 토큰이 유효하지 않거나 만료되었습니다",
                                                      "content": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/token/reissue")
    public ResponseEntity<ApiResponse<Void>> reIssueServiceToken(
            @RequestHeader("Authorization") final String accessToken,
            @CookieValue("refreshToken") final String refreshToken
    );

}
