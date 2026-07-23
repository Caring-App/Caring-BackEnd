package com.caring.domain.schedule.repository;

import com.caring.domain.schedule.entity.TaskSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TaskScheduleRepository extends JpaRepository<TaskSchedule, Long> {
    List<TaskSchedule> findByWard_MemberIdAndTaskDateOrderByTaskTimeAsc(Long wardId, LocalDate taskDate);
    List<TaskSchedule> findByTaskDateAndTtsVoiceTime(LocalDate taskDate, LocalTime ttsVoiceTime);
}
