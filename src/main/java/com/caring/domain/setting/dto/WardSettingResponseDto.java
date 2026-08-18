package com.caring.domain.setting.dto;

import com.caring.domain.setting.entity.WardSetting;
import com.caring.global.common.FontSize;
import lombok.Getter;

@Getter
public class WardSettingResponseDto {
    private final Long wardSettingId;
    private final Long wardId;
    private final FontSize fontSize;
    private final Double ttsRate;

    public WardSettingResponseDto(WardSetting wardSetting) {
        this.wardSettingId = wardSetting.getWardSettingId();
        this.wardId = wardSetting.getMember().getMemberId();
        this.fontSize = wardSetting.getFontSize();
        this.ttsRate = wardSetting.getTtsRate();
    }
}
