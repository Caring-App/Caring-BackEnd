package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Provider;
import com.caring.domain.member.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class SocialRegisterRequestDto {
    // 소셜 회원가입 요청 DTO

    private Provider provider; // 로그인 응답에서 돌려받은 값 그대로
    private String providerId; // 로그인 응답에서 돌려받은 값 그대로

    private Role role; // PROTECTOR 또는 WARD
    private String name;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private List<String> diseases; // WARD일 때만 사용, PROTECTOR면 그냥 null
}
