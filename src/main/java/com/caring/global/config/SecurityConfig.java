package com.caring.global.config;


import com.caring.global.jwt.JwtAuthenticationFilter;
import com.caring.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 설정 파일이라는 것을 spring에게 알려주는 것
@EnableWebSecurity // Spring Security 기능 켠다고 선언
@RequiredArgsConstructor // final 필드 생성자 자동으로 만들어주는 것
public class SecurityConfig {

    private final JwtUtil jwtUtil; // JWT 필터 만들 때 필요

    // 실제 보안 규칙을 담는 메소드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // HttpSecurity = 보안 규칙 설정하는 도구, throws Exception = 설정 중 오류날 수 있어서 예외

        // CSRF 비활성화
        http.csrf(csrf->csrf.disable()) // csrf.disable() = CSRF 보호 기능 끄기
                // 세션 정책 설정
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL 접근 권한 설정, 어디는 누구나, 어디는 로그인만
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 이 URL들은
                                "/api/auth/**",
                                "/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/policy/**"
                        ).permitAll() // 누구나 접근 가능
                        .anyRequest().authenticated() // 나머지는 로그인 필수
                )
                .addFilterBefore( // 이 필터를 앞에 끼워넣기
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}