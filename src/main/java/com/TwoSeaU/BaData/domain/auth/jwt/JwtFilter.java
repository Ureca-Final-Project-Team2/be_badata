package com.TwoSeaU.BaData.domain.auth.jwt;

import com.TwoSeaU.BaData.global.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final String AUTHORIZATION_KEY = "Authorization";
    private final ServiceTokenProvider serviceTokenProvider;
    private final RedisUtil redisUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = parseHeader(request);

        if(StringUtils.hasText(tokenValue) && serviceTokenProvider.validateToken(tokenValue)) {

            String logOut= redisUtil.getValueByKey(tokenValue);
            if(ObjectUtils.isEmpty(logOut)){
                Authentication authentication = serviceTokenProvider.getAuthentication(tokenValue);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public String parseHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_KEY);

        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}

