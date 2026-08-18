package com.caring.domain.connection.dto;

import com.caring.global.common.FontSize;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WardSummaryResponseDto {
    private Long connectionId;
    private Long wardId;
    private String wardName;
    private String nickname;
    private LocalDateTime linkedAt;
    private FontSize fontSize;
    private Double ttsRate;
}
