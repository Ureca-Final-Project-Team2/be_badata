package com.TwoSeaU.BaData.domain.auth.controller.swagger;

import com.TwoSeaU.BaData.domain.auth.dto.response.CheckNewUserResponse;
import com.TwoSeaU.BaData.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
                                            name = "RefreshToken",
                                            description = "발급된 Refresh Token",
                                            schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
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
                                                            "newUser": false
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
    ResponseEntity<ApiResponse<CheckNewUserResponse>> getToken(
            @Parameter(description = "소셜 로그인 후 받은 code") @RequestParam("code") String code,
            @Parameter(description = "소셜 로그인 제공자 (예: kakao)") @RequestParam("provider") String provider
    );

}
