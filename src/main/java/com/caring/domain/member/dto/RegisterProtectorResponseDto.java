package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterProtectorResponseDto {
    private Long memberId;
    private String name;
    private String phone;
    private String protectorCode;

    public static RegisterProtectorResponseDto of(Member member){
        return RegisterProtectorResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .phone(member.getPhone())
                .protectorCode(member.getProtectorCode())
                .build();
    }
}
