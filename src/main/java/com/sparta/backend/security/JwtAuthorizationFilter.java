package com.sparta.backend.security;

import com.google.gson.Gson;
import com.sparta.backend.user.model.UserRoleEnum;
import com.sparta.backend.user.repository.RefreshTokenRedisRepository;
import com.sparta.backend.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        if (req.getRequestURI().equals("/api/user/refresh") || req.getRequestURI().equals("/foot/chat") || req.getRequestURI().equals("/foot/chat/info")) {
            filterChain.doFilter(req, res);
            return;
        }

        log.info(req.getRequestURI());


        String tokenValue = jwtUtil.getJwtFromHeader(req);

        log.info("doFilterInternal");
        if (StringUtils.hasText(tokenValue)) {
            log.info("StringUtils.hasText");
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
            log.info("Claims");
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                res.setStatus(HttpStatus.SC_UNAUTHORIZED);

                return;

            }

            setAuthentication(info.getSubject());
        }
        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}