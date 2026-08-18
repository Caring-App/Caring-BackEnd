package com.caring.global.common;

public class AlarmValidationUtil {

    public static void  validateVoiceSetting(AlarmType alarmType, String voiceFileUrl) {
        if (alarmType == AlarmType.VOICE_RECORD && (voiceFileUrl == null || voiceFileUrl.isBlank())) {
            throw new IllegalArgumentException("보호자가 음성 녹음을 선택했지만 녹음 파일이 없습니다.");
        }
    }
}
