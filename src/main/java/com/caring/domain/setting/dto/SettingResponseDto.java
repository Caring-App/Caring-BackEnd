package com.caring.domain.setting.dto;

import com.caring.domain.setting.entity.Setting;
import com.caring.global.common.FontSize;
import lombok.Getter;

@Getter
public class SettingResponseDto {
    private final Long settingId;
    private final Long wardId;
    private final FontSize fontSize;
    private final Double ttsRate;

    public SettingResponseDto(Setting setting) {
        this.settingId = setting.getSettingId();
        this.wardId = setting.getMember().getMemberId();
        this.fontSize = setting.getFontSize();
        this.ttsRate = setting.getTtsRate();
    }
}
