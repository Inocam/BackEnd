package com.sparta.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class CustomSecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomSecurityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Request URI: {}", request.getRequestURI());
        logger.info("Request Method: {}", request.getMethod());

        // 인증 및 인가가 이루어지기 전에 필터링 작업 가능
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error occurred during filtering: {}", e.getMessage());
            throw e;
        }

        logger.info("Response Status: {}", response.getStatus());
    }
}