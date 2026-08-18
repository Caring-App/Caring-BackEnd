package com.caring.domain.setting.dto;

import com.caring.global.common.FontSize;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WardSettingRequestDto {
    @NotNull
    private FontSize fontSize;

    @NotNull
    @DecimalMin(value = "0.25")
    @DecimalMax(value = "4.0")
    private Double ttsRate;
}
