package com.caring.domain.connection.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WardSummaryResponseDto {
    private Long connentionId;
    private Long wardId;
    private String wardName;
    private LocalDateTime linkedAt;
}
