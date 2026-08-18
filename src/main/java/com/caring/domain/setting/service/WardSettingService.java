package com.caring.domain.setting.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.pill.repository.PillScheduleRepository;
import com.caring.domain.pill.service.PillScheduleService;
import com.caring.domain.schedule.service.TaskScheduleService;
import com.caring.domain.setting.dto.WardSettingRequestDto;
import com.caring.domain.setting.dto.WardSettingResponseDto;
import com.caring.domain.setting.entity.WardSetting;
import com.caring.domain.setting.repository.WardSettingRepository;
import com.caring.global.common.FontSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WardSettingService {
    private final WardSettingRepository wardSettingRepository;
    private final ConnectionRepository connectionRepository;
    private final PillScheduleService pillScheduleService;
    private final TaskScheduleService taskScheduleService;

    @Transactional
    public void createDefaultSetting(Member ward) {
        WardSetting wardSetting = WardSetting.builder()
                .member(ward)
                .fontSize(FontSize.MEDIUM)
                .ttsRate(1.0)
                .build();
        wardSettingRepository.save(wardSetting);
    }


    public WardSettingResponseDto getSetting(Long protectorId, Long wardId) {
        validateProtectorOfWard(protectorId, wardId);

        WardSetting wardSetting = wardSettingRepository.findByMember_MemberId(wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자의 설정 정보가 존재하지 않습니다."));

        return new WardSettingResponseDto(wardSetting);
    }


    @Transactional
    public WardSettingResponseDto updateSetting(Long protectorId, Long wardId, WardSettingRequestDto requestDto) {
        validateProtectorOfWard(protectorId, wardId);

        WardSetting wardSetting = wardSettingRepository.findByMember_MemberId(wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자의 설정 정보가 존재하지 않습니다."));

        boolean rateChanged = !wardSetting.getTtsRate().equals(requestDto.getTtsRate());

        wardSetting.updateSetting(requestDto.getFontSize(), requestDto.getTtsRate());

        if(rateChanged) {
            Member ward = wardSetting.getMember();
            pillScheduleService.resynthesizeAllForWard(ward, requestDto.getTtsRate());
            taskScheduleService.resynthesizeAllForWard(ward, requestDto.getTtsRate());
        }

        return new WardSettingResponseDto(wardSetting);
    }


    private void validateProtectorOfWard(Long protectorId, Long wardId) {
        if(!connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId)) {
            throw new IllegalArgumentException("본인과 연결된 돌봄대상자가 아닙니다.");
        }
    }
}
