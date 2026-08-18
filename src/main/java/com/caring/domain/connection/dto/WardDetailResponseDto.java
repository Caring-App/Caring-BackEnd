package com.caring.domain.connection.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WardDetailResponseDto {
    private Long wardId;
    private String wardName;
    private String nickname;
    private String phone;
    private String address;
}
