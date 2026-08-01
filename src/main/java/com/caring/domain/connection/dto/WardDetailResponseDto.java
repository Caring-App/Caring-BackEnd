package com.caring.domain.connection.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WardDetailResponseDto {
    private Long connectionId;
    private Long wardId;
    private String wardName;
    private String nickname;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private List<String> diseases;
    private LocalDateTime linkedAt;
}
