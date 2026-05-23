package com.caring.global.config;

import com.caring.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// final 필드의 생성자 직접 만들어주는 어노테이션

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // 소셜 로그인 성공 시 실행되는 클래스, JWT 토큰 만들어서 프론트로 전달해줌
    //SimpleUrlAuthenticationSuccessHandler
    // 소셜 로그인 성공시 처리하는 spring 기본 클래스, 상속 받아서 덮어씌우기

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 로그인 한 사람 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // .getPrincipal() = 로그인한 사람 정보 꺼내기

        //memberId 가져오기
        Long memberId = ((Number)oAuth2User.getAttributes().get("id")).longValue();
        String role = "USER";

        // 토큰 만들기
        String accessToken = jwtUtil.generateAccessToken(memberId,role);
        String refreshToken = jwtUtil.generateRefreshToken(memberId,role);

        // 프론트로 토큰 전달하기 , 리다이렉트 + URL에 담기
        String redirectUrl = "/oauth2/redirect"
                +"?accessToken="+accessToken
                +"&refreshToken="+refreshToken;

        getRedirectStrategy().sendRedirect(request,response,redirectUrl);
        // getRedirectStrategy().sendRedirect() = 그 URL로 이동시키기

    }
}
