package com.caring.domain.member.dto;

import com.caring.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegisterWardResponseDto {
    private Long memberId;
    private String name;
    private String phone;
    private List<String> diseases;

    public static RegisterWardResponseDto of(Member member, List<String> diseaseNames) {
        return RegisterWardResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .phone(member.getPhone())
                .diseases(diseaseNames)
                .build();
    }
}
