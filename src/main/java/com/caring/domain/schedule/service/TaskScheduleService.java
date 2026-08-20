package com.caring.domain.schedule.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.schedule.dto.TaskScheduleRequestDto;
import com.caring.domain.schedule.dto.TaskScheduleResponseDto;
import com.caring.domain.schedule.entity.TaskSchedule;
import com.caring.domain.schedule.repository.TaskScheduleRepository;
import com.caring.domain.setting.entity.WardSetting;
import com.caring.domain.setting.repository.WardSettingRepository;
import com.caring.global.common.AlarmType;
import com.caring.global.common.AlarmValidationUtil;
import com.caring.global.tts.service.TtsFileService;
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
    private final TtsFileService ttsFileService;
    private final WardSettingRepository wardSettingRepository;

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

        String voiceFileUrl = resolveVoiceFileUrl(ward, requestDto);

        TaskSchedule taskSchedule = TaskSchedule.builder()
                .ward(ward)
                .taskName(requestDto.getTaskName())
                .locationName(requestDto.getLocationName())
                .taskDate(requestDto.getTaskDate())
                .taskTime(requestDto.getTaskTime())
                .ttsVoiceTime(requestDto.getTtsVoiceTime())
                .ttsMessage(requestDto.getTtsMessage())
                .alarmType(requestDto.getAlarmType())
                .voiceFileUrl(voiceFileUrl)
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

        String voiceFileUrl = resolveVoiceFileUrl(taskSchedule.getWard(), requestDto);

        taskSchedule.updateTask(
                requestDto.getTaskName(),
                requestDto.getLocationName(),
                requestDto.getTaskDate(),
                requestDto.getTaskTime(),
                requestDto.getTtsVoiceTime(),
                requestDto.getTtsMessage(),
                requestDto.getAlarmType(),
                voiceFileUrl,
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


    @Transactional
    public void resynthesizeAllForWard(Member ward, double newTtsRate) {
        List<TaskSchedule> schedules = taskScheduleRepository.findByWard_MemberId(ward.getMemberId());

        for(TaskSchedule schedule : schedules) {
            if(schedule.getAlarmType() != AlarmType.TTS) {
                continue;
            }

            String message = (schedule.getTtsMessage() != null && !schedule.getTtsMessage().isBlank())
                    ? schedule.getTtsMessage()
                    : buildTtsMessageFromSchedule(ward, schedule);

            String newVoiceFileUrl = ttsFileService.synthesizeAndUpload(message, newTtsRate);
            schedule.updateVoiceFile(newVoiceFileUrl);
        }
    }

    private String resolveVoiceFileUrl(Member ward, TaskScheduleRequestDto requestDto) {
        AlarmValidationUtil.validateVoiceSetting(requestDto.getAlarmType(), requestDto.getVoiceFileUrl());

        if(requestDto.getAlarmType() == AlarmType.TTS) {
            String message = (requestDto.getTtsMessage() != null && !requestDto.getTtsMessage().isBlank())
                    ? requestDto.getTtsMessage()
                    : buildTtsMessage(ward, requestDto);
            double ttsRate = resolveTtsRate(ward);
            return ttsFileService.synthesizeAndUpload(message,ttsRate);
        }
        return requestDto.getVoiceFileUrl();
    }


    private String buildTtsMessage(Member ward, TaskScheduleRequestDto requestDto) {
        String location = (requestDto.getLocationName() != null && !requestDto.getLocationName().isBlank())
                ? " (" + requestDto.getLocationName() + ") "
                : "";
        return String.format("%s 어르신, %s%s 일정이 %s에 있어요.",
                ward.getName(), requestDto.getTaskName(), location, requestDto.getTaskTime());
    }


    private double resolveTtsRate(Member ward) {
        return wardSettingRepository.findByMember(ward)
                .map(WardSetting::getTtsRate)
                .orElse(1.0);
    }


    private String buildTtsMessageFromSchedule(Member ward, TaskSchedule schedule) {
        String location = (schedule.getLocationName() != null && !schedule.getLocationName().isBlank())
                ? " (" + schedule.getLocationName() + ")" : "";
        return ward.getName() + " 어르신, " + schedule.getTaskName() + location
                + " 일정이 " + schedule.getTaskTime() + "에 있어요.";
    }
}