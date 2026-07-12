package com.caring.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    TTS("기본 TTS 음성 안내"),
    VOICE_RECORD("보호자 녹음 음성 안내");

    private final String description;
}
