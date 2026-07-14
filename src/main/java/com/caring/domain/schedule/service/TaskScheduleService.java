package com.caring.domain.schedule.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.schedule.dto.TaskScheduleRequestDto;
import com.caring.domain.schedule.dto.TaskScheduleResponseDto;
import com.caring.domain.schedule.entity.TaskSchedule;
import com.caring.domain.schedule.repository.TaskScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskScheduleService {

    private final TaskScheduleRepository taskScheduleRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;

    private void validateProtectorOfWard(Long protectorId, Long wardId) {
        boolean isConnected = connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId);
        if (!isConnected) {
            throw new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다.");
        }
    }

    @Transactional
    public TaskScheduleResponseDto createTask(Long protectorId, Long wardId, TaskScheduleRequestDto requestDto) {
        validateProtectorOfWard(protectorId, wardId);

        Member ward = memberRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자가 존재하지 않습니다. ID = " + wardId));

        TaskSchedule taskSchedule = TaskSchedule.builder()
                .ward(ward)
                .taskName(requestDto.getTaskName())
                .locationName(requestDto.getLocationName())
                .taskDate(requestDto.getTaskDate())
                .taskTime(requestDto.getTaskTime())
                .ttsVoiceTime(requestDto.getTtsVoiceTime())
                .ttsMessage(requestDto.getTtsMessage())
                .alarmType(requestDto.getAlarmType())
                .voiceFileUrl(requestDto.getVoiceFileUrl())
                .placeId(requestDto.getPlaceId())
                .build();

        return new TaskScheduleResponseDto(taskScheduleRepository.save(taskSchedule));
    }


    public List<TaskScheduleResponseDto> getTasksByDate(Long protectorId, Long wardId, LocalDate date) {
        validateProtectorOfWard(protectorId, wardId);

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        return taskScheduleRepository.findByWard_MemberIdAndTaskDateOrderByTaskTimeAsc(wardId, targetDate)
                .stream()
                .map(TaskScheduleResponseDto::new)
                .toList();
    }


    @Transactional
    public TaskScheduleResponseDto updateTask(Long protectorId, Long taskId, TaskScheduleRequestDto requestDto) {
        TaskSchedule taskSchedule = taskScheduleRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. ID = " + taskId));

        validateProtectorOfWard(protectorId, taskSchedule.getWard().getMemberId());

        taskSchedule.updateTask(
                requestDto.getTaskName(),
                requestDto.getLocationName(),
                requestDto.getTaskDate(),
                requestDto.getTaskTime(),
                requestDto.getTtsVoiceTime(),
                requestDto.getTtsMessage(),
                requestDto.getAlarmType(),
                requestDto.getVoiceFileUrl(),
                requestDto.getPlaceId()
        );

        return new TaskScheduleResponseDto(taskSchedule);
    }


    @Transactional
    public void deleteTask(Long protectorId, Long taskId) {
        TaskSchedule taskSchedule = taskScheduleRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. ID = " + taskId));

        validateProtectorOfWard(protectorId, taskSchedule.getWard().getMemberId());

        taskScheduleRepository.delete(taskSchedule);
    }
}