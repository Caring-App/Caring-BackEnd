package com.caring.domain.health.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HealthRecordRequestDto {

    private Long diseaseId;
    private Integer healthValue;

}
