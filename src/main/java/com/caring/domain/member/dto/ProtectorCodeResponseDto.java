package com.caring.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProtectorCodeResponseDto {
    private String protectorCode;
}