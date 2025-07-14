package com.TwoSeaU.BaData.domain.auth.config;

import com.TwoSeaU.BaData.domain.auth.jwt.JwtFilter;
import com.TwoSeaU.BaData.domain.auth.jwt.handler.JwtAccessDeniedHandler;
import com.TwoSeaU.BaData.domain.auth.jwt.handler.JwtAuthenticationEntryPointHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtFilter jwtFilter;
    /**
     * Spring Security의 HTTP 요청 보안 정책을 구성하고 SecurityFilterChain을 반환합니다.
     *
     * CORS를 기본 설정으로 활성화하고, CSRF, 폼 로그인, HTTP Basic 인증을 비활성화합니다.
     * 세션 관리를 무상태(STATELESS)로 설정하며, JWT 기반 인증 및 인가 예외 핸들러를 적용합니다.
     * Swagger 및 API 문서, 인증 및 일부 거래 게시글 관련 엔드포인트는 인증 없이 접근을 허용하고,
     * 그 외 모든 요청은 인증을 요구합니다.
     * UsernamePasswordAuthenticationFilter 앞에 JwtFilter를 추가하여 JWT 인증을 처리합니다.
     *
     * @param http Spring Security의 HttpSecurity 객체
     * @return 구성된 SecurityFilterChain 인스턴스
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exception)-> exception.authenticationEntryPoint(authenticationEntryPointHandler))
                .exceptionHandling((exception)-> exception.accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/v1/auth/token/issue").permitAll()
                                .requestMatchers("/api/v1/trades/posts").permitAll()
                                .requestMatchers("/api/v1/trades/posts/{userId}").permitAll()
                                .requestMatchers("/api/v1/trades/posts/deadline").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/v1/auth/token/reissue","/api/v1/stores/**","/api/v1/stores").permitAll()
                                .anyRequest().authenticated())

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
