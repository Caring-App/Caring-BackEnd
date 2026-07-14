package com.caring.domain.schedule.controller;

import com.caring.domain.schedule.dto.TaskScheduleRequestDto;
import com.caring.domain.schedule.dto.TaskScheduleResponseDto;
import com.caring.domain.schedule.service.TaskScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/task-schedule")
@RequiredArgsConstructor
public class TaskScheduleController {

    private final TaskScheduleService taskScheduleService;

    @PostMapping
    public ResponseEntity<TaskScheduleResponseDto> createTask(
            @AuthenticationPrincipal Long protectorId,
            @RequestParam Long wardId,
            @RequestBody TaskScheduleRequestDto requestDto
    ) {
        return ResponseEntity.ok(taskScheduleService.createTask(protectorId, wardId, requestDto));
    }

    @GetMapping
    public ResponseEntity<List<TaskScheduleResponseDto>> getTasks(
            @AuthenticationPrincipal Long protectorId,
            @RequestParam Long wardId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(taskScheduleService.getTasksByDate(protectorId, wardId, date));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskScheduleResponseDto> updateTask(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long taskId,
            @RequestBody TaskScheduleRequestDto requestDto
    ) {
        return ResponseEntity.ok(taskScheduleService.updateTask(protectorId, taskId, requestDto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long taskId) {
        taskScheduleService.deleteTask(protectorId, taskId);
        return ResponseEntity.noContent().build();
    }
}