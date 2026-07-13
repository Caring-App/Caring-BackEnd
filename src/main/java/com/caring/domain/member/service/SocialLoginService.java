package com.caring.domain.member.service;

import com.caring.domain.member.dto.SocialUserInfo;
import com.caring.domain.member.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final RestClient restClient;
    // Spring 프레임워크가 만들어놓은 HTTP 통신 도구 클래스
    // 다른 서버에게 요청 보내고 응답 받아옴

    // 컨트롤러가 실제로 호출 할 진입 포인트 - provider에 따라 알맞은 case로
    public SocialUserInfo getSocialUserInfo (Provider provider, String accessToken) {
        return switch (provider){
            case KAKAO -> getKakaoUserInfo(accessToken);
            case NAVER -> getNaverUserInfo(accessToken);
            case GOOGLE -> getGoogleUserInfo(accessToken);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: "+provider);
        };
    }

    // 각각의 소셜 로그인 메소드 3개
    // API 호출 -> JSON을 MAP으로 받음 -> 원하는 id 값만 형변환하여 꺼냄 -> SocialUserInfo로 포장해서 반환

    private SocialUserInfo getKakaoUserInfo(String accessToken){

        // 카카오 사용자 정보 조회 API를 GET으로 호출
        // 이 accessToken이 진짜 카카오가 발급한 유효한 토큰이 아니면
        // 여기서 카카오 서버가 401(인증 실패)을 돌려줌
        // -> 즉 이 호출 자체가 토큰이 진짜인지 확인하는 검증 역할을 겸함
        Map<String, Object> response = restClient.get() // GET 방식으로 요청
                .uri("https://kapi.kakao.com/v2/user/me") // 요청 주소 설정
                .header("Authorization","Bearer "+accessToken) // 헤더 설정 -> 카카오가 요구하는 인증 헤더 형식 (Bearer + 토큰)
                .retrieve() // 요청 전송
                .body(Map.class); // 응답 JSON 본문을 Map<String, Object>로 자동 변환해서 받음

        // response.get()은 반환 타입이 Object라서, 원래 타입(Long)으로 형변환
        Long providerId = (Long) response.get("id");

        return SocialUserInfo.builder()
                .provider(Provider.KAKAO)
                .providerId(providerId.toString())
                .build();
    }

    private SocialUserInfo getNaverUserInfo(String accessToken){

        // 네이버 사용자 정보 조회 API 호출
        // accessToken이 유효하지 않으면 여기서 에러
        Map<String,Object> response = restClient.get() // GET 방식으로 요청
                .uri("https://openapi.naver.com/v1/nid/me")// 요청 주소 설정
                .header("Authorization", "Bearer " + accessToken)// 인증 헤더
                .retrieve() // 요청 전송
                .body(Map.class); // 응답 JSON을 Map으로 변환

        // 네이버 응답 예시: { "resultcode": "00", "response": { "id": "32742776", "email": "..." } }
        // response.get("response")는 또 하나의 Map이라, 이것도 형변환 필요
        Map<String,Object> naverAccount = (Map<String, Object>) response.get("response");
        String providerId = (String) naverAccount.get("id"); // 네이버는 id가 이미 String

        return SocialUserInfo.builder()
                .provider(Provider.NAVER)
                .providerId(providerId)
                .build();
    }
    private SocialUserInfo getGoogleUserInfo(String accessToken){

        // 구글 사용자 정보 조회 API 호출
        // accessToken이 유효하지 않으면 여기서 에러
        Map<String, Object> response = restClient.get() // GET 방식으로 요청
                .uri("https://www.googleapis.com/oauth2/v3/userinfo") // 요청 주소 설정
                .header("Authorization","Bearer "+accessToken) // 인증 헤더
                .retrieve() // 요청 전송
                .body(Map.class); // 응답 JSON을 Map으로 변환

        // 구글 응답 예시: { "sub": "110169484474386276334", "email": "...", ... }
        String providerId = (String) response.get("sub");

        return SocialUserInfo.builder()
                .provider(Provider.GOOGLE)
                .providerId(providerId.toString())
                .build();
    }
}

