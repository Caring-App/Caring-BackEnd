package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SocialRegisterResponseDto {
    // 가입 완료 후 로그인까지 해주는 응답 DTO

    private Long memberId;
    private String name;
    private String phone;
    private Role role;
    private String protectorCode; //PROTECTOR일 때만 값 채워짐, WARD면 null
    private List<String> diseases;  // WARD일 때만 값 채워짐, PROTECTOR면 null
    private String accessToken;
    private String refreshToken;
    
}
