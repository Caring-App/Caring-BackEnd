package com.caring.domain.pill.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.pill.dto.PillScheduleRequestDto;
import com.caring.domain.pill.dto.PillScheduleResponseDto;
import com.caring.domain.pill.entity.PillSchedule;
import com.caring.domain.pill.repository.PillScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PillScheduleService {

    private final PillScheduleRepository pillScheduleRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;

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

        PillSchedule pillSchedule = PillSchedule.builder()
                .ward(ward)
                .pillName(requestDto.getPillName())
                .takeDays(requestDto.getTakeDays())
                .takeTime(requestDto.getTakeTime())
                .retryAlarm(requestDto.getRetryAlarm())
                .alarmType(requestDto.getAlarmType())
                .voiceFileUrl(requestDto.getVoiceFileUrl())
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

        pillSchedule.updateSchedule(
                requestDto.getPillName(),
                requestDto.getTakeDays(),
                requestDto.getTakeTime(),
                requestDto.getRetryAlarm(),
                requestDto.getAlarmType(),
                requestDto.getVoiceFileUrl()
        );

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
}
