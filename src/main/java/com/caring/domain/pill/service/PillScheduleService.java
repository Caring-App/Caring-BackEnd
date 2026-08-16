package com.caring.domain.pill.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.pill.dto.PillScheduleRequestDto;
import com.caring.domain.pill.dto.PillScheduleResponseDto;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.entity.PillType;
import com.caring.domain.pill.repository.PillLogRepository;
import com.caring.domain.pill.repository.PillScheduleRepository;
import com.caring.global.common.AlarmType;
import com.caring.global.common.AlarmValidationUtil;
import com.caring.global.tts.service.TtsFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PillScheduleService {

    private final PillScheduleRepository pillScheduleRepository;
    private final PillLogRepository pillLogRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;
    private final TtsFileService ttsFileService;

    private void validateProtectorOfWard(Long protectorId, Long wardId) {
        boolean isConnected = connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId);
        if (!isConnected) {
            throw new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다.");
        }
    }

    // 복약 일정 등록
    @Transactional
    public PillScheduleResponseDto createSchedule(Long protectorId, PillScheduleRequestDto requestDto) {
        validateProtectorOfWard(protectorId, requestDto.getWardId());

        Member ward = memberRepository.findById(requestDto.getWardId())
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자가 존재하지 않습니다. ID = " + requestDto.getWardId()));

        String voiceFileUrl = resolveVoiceFileUrl(ward, requestDto);

        PillSchedule pillSchedule = PillSchedule.builder()
                .ward(ward)
                .pillName(requestDto.getPillName())
                .takeDays(requestDto.getTakeDays())
                .takeTime(requestDto.getTakeTime())
                .retryAlarm(requestDto.getRetryAlarm())
                .alarmType(requestDto.getAlarmType())
                .voiceFileUrl(voiceFileUrl)
                .isActive(requestDto.isActive())
                .build();

        PillSchedule savedSchedule = pillScheduleRepository.save(pillSchedule);
        return new PillScheduleResponseDto(savedSchedule);
    }


    // 돌봄 대상자의 복약 일정 리스트 조회
    public List<PillScheduleResponseDto> getSchedulesByWard(Long protectorId, Long wardId) {
        validateProtectorOfWard(protectorId, wardId);

        return pillScheduleRepository.findByWardMemberId(wardId).stream()
                .map(PillScheduleResponseDto::new)
                .collect(Collectors.toList());
    }


    // 복약 일정 내용 수정
    @Transactional
    public PillScheduleResponseDto updateSchedule(Long protectorId, Long id, PillScheduleRequestDto requestDto) {
        PillSchedule pillSchedule = pillScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 복약 일정이 존재하지 않습니다. ID = " + id));

        validateProtectorOfWard(protectorId, pillSchedule.getWard().getMemberId());

        String voiceFileUrl = resolveVoiceFileUrl(pillSchedule.getWard(), requestDto);

        pillSchedule.updateSchedule(
                requestDto.getPillName(),
                requestDto.getTakeDays(),
                requestDto.getTakeTime(),
                requestDto.getRetryAlarm(),
                requestDto.getAlarmType(),
                voiceFileUrl
        );

        pillLogRepository.findByPillScheduleAndRecordDate(pillSchedule, LocalDate.now())
                .ifPresent(pillLog -> {
                    if(!pillLog.isTaken()) {
                        pillLog.resetForRetry();
                    }
                });

        return new PillScheduleResponseDto(pillSchedule);
    }


    // 알림 ON/OFF 토글 상태 수정
    @Transactional
    public PillScheduleResponseDto toggleAlarmStatus(Long protectorId, Long id, boolean isActive) {
        PillSchedule pillSchedule = pillScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 해당 복약 일정이 존재하지 않습니다. ID = " + id));

        validateProtectorOfWard(protectorId, pillSchedule.getWard().getMemberId());

        pillSchedule.toggleActiveStatus(isActive);
        return new PillScheduleResponseDto(pillSchedule);
    }


    // 복약 일정 삭제
    @Transactional
    public void deleteSchedule(Long protectorId, Long id) {
        PillSchedule pillSchedule = pillScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 해당 복약 일정이 존재하지 않습니다. ID = " + id));

        validateProtectorOfWard(protectorId, pillSchedule.getWard().getMemberId());

        pillScheduleRepository.delete(pillSchedule);
    }


    private String resolveVoiceFileUrl(Member ward, PillScheduleRequestDto requestDto) {
        AlarmValidationUtil.validateVoiceSetting(requestDto.getAlarmType(), requestDto.getVoiceFileUrl());

        if (requestDto.getAlarmType() == AlarmType.TTS) {
            String message = buildInitialPillMessage(ward, requestDto.getPillName());
            return ttsFileService.synthesizeAndUpload(message);
        }
        return requestDto.getVoiceFileUrl();
    }


    private String buildInitialPillMessage(Member ward, PillType pillName) {
        return ward.getName() + " 어르신, " + pillName.getDescription() + " 드실 시간입니다! 잊지 말고 챙겨 드세요.";
    }
}
