package com.caring.global.config;


import com.caring.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 설정 파일이라는 것을 spring에게 알려주는 것
@EnableWebSecurity // Spring Security 기능 켠다고 선언
@RequiredArgsConstructor // final 필드 생성자 자동으로 만들어주는 것
public class SecurityConfig {

    private final JwtUtil jwtUtil; // JWT 필터 만들 때 필요
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // 소셜 로그인 성공 시 필요

    // 실제 보안 규칙을 담는 메소드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // HttpSecurity = 보안 규칙 설정하는 도구, throws Exception = 설정 중 오류날 수 있어서 예외

    }
}
