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

        String tokenValue = jwtUtil.getJwtFromHeader(req);

        if (StringUtils.hasText(tokenValue)) {

            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        filterChain.doFilter(req, res);

        //        String tokenValue = jwtUtil.getJwtFromHeader(req);
//        log.debug("doFilterInternal");
//        if (StringUtils.hasText(tokenValue)) {
//            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
//
//            if (!jwtUtil.validateToken(tokenValue)) {
//                log.error("Token Error");
//
//                String email = info.getSubject();
//                if (refreshTokenRedisRepository.existsByKey(email)) {
//                    String newToken = jwtUtil.createAccessToken(email, UserRoleEnum.USER);
//
//                    // JSON 객체 생성
//                    Map<String, String> userInfo = new HashMap<>();
//                    userInfo.put("accessToken", newToken);
//                    userInfo.put("id", userRepository.findByEmail(email).get().getId().toString());
//                    userInfo.put("username", userRepository.findByEmail(email).get().getUsername().toString());
//                    userInfo.put("email", email);
//
//                    // 응답 본문에 JSON 작성
//                    res.setContentType("application/json");
//                    res.setCharacterEncoding("UTF-8");
//                    try (PrintWriter out = res.getWriter()) {
//                        out.print(new Gson().toJson(userInfo)); // Gson 라이브러리를 사용하여 JSON 변환
//                        out.flush();
//                    } catch (IOException e) {
//                        log.error("Failed to send response", e);
//                    }
//
//                    // 새로운 토큰 정보로 인증 설정
//                    info = jwtUtil.getUserInfoFromToken(newToken);
//                } else {
//                    // 유효하지 않은 토큰이므로 필터 체인을 호출하지 않고 반환
//                    return;
//                }
//            }
//
//            try {
//                setAuthentication(info.getSubject());
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                return;
//            }
    ///    }

        //filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        log.debug("setAuthentication");

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