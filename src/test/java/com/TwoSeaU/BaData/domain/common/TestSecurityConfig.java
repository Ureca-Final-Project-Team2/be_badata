package com.TwoSeaU.BaData.domain.common;

import com.TwoSeaU.BaData.domain.auth.jwt.handler.JwtAccessDeniedHandler;
import com.TwoSeaU.BaData.domain.auth.jwt.handler.JwtAuthenticationEntryPointHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@TestConfiguration
public class TestSecurityConfig {

    private final JwtAuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public TestSecurityConfig(){

        this.authenticationEntryPointHandler = new JwtAuthenticationEntryPointHandler(new ObjectMapper());
        this.accessDeniedHandler = new JwtAccessDeniedHandler(new ObjectMapper());
    }

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
                                .requestMatchers(HttpMethod.GET, "/api/v1/trades/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/v1/auth/token/reissue","/api/v1/stores/**","/api/v1/stores","/api/v1/rentals/{storeId}/devices","/api/v1/review-quick-replies"
                                        ,"/api/v1/{storeId}/reviews","/api/v1/{storeId}/review-meta","/test/alarm").permitAll()
                                .anyRequest().authenticated());

        return http.build();
    }

}
