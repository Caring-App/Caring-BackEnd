package com.caring.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private long accessTokenExpiry;
    private long refreshTokenExpiry;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // Keys.hmacShaKeyFor() 문자열을 SecretKey로 변환해주는 메소드
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    //반환타입이 String 인 이유는 JWT 토큰은 결국 긴 문자열
    // 로그인 성공 시 토큰 만들어주는 메소드
    public String generateAccessToken(Long memberId, String role){
        // 토큰 발급 시간
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 토큰 주인 누구야 + memberId 문자열로 변환
                .claim("role",role) // 토큰 안에 추가 정보 넣기 -> 이 사람 role이 뭔지
                .issuedAt(now) // 언제 만들었는지, 위에서 발급한 now 넣기
                .expiration(new Date(now.getTime()+accessTokenExpiry))
                // 만료 시간 추가 -> 지금 시간을 숫자로 변환 + 거기에 30분 더하기
                .signWith(secretKey) // 토큰에 서명하는 느낌 + 우리만 아는 키로 서명해야 인증
                .compact(); // 여태까지 적은 정보를 하나의 긴 JWT 문자열로 완성시키기
    }

    //  Refresh Token 만드는 메소드, Access Token이 만료됐을 때 다시 발급받기 위한 재발급 토큰
    public String generateRefreshToken (Long memberId, String role){
        // 토큰 발급 시간
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("role",role)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+refreshTokenExpiry))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검증하는 메소드
    public boolean validateToken(String token) {

        // 토큰이 이상하면 예외 터지기에 잡으려고 try catch문 사용함
        try {
            getClaims(token); // 토큰 파싱
            return true;

        }catch (JwtException | IllegalArgumentException e){
            // JwtException = JWT 관련 모든 예외, IllegalArgumentException = 토큰이 null이거나 빈 값
            return false;
        }
    }

    // 토큰 열어서 안에 있는 정보를 꺼내는 메소드
    private Claims getClaims(String token) {
        return Jwts.parser() // 토큰 열 준비
                .verifyWith(secretKey)// 우리 키로 확인
                .build() //  파서 완성
                .parseSignedClaims(token) // 토큰 실제로 파싱
                .getPayload(); // 안에 있는 정보 꺼내기 -> 토큰의 전체 덩어리
    }

    // memberid 꺼내는 메소드
    // getClaims -> .getPayload() 로 꺼낸 전체 토큰에서 id만 가져오기
    public Long getMemberId(String token) {
        // 전체 토큰에서 subject만 꺼내기
        return Long.parseLong(getClaims(token).getSubject());
    }
    // getClaims -> .getPayload() 로 꺼낸 전체 토큰에서 role만 가져오기
    public String getRole(String token) {
        return getClaims(token).get("role",String.class);
        // get("role") = "role" 이라는 이름으로 넣은 값 꺼내고,
        // String.class = String 타입으로 꺼내기
    }
}
