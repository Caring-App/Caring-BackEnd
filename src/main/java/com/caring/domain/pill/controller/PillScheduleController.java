package com.caring.domain.pill.controller;

import com.caring.domain.pill.dto.PillScheduleRequestDto;
import com.caring.domain.pill.dto.PillScheduleResponseDto;
import com.caring.domain.pill.service.PillScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pill")
@RequiredArgsConstructor
public class PillScheduleController {

    private final PillScheduleService pillScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<PillScheduleResponseDto> createSchedule(
            @AuthenticationPrincipal Long protectorId,
            @RequestBody PillScheduleRequestDto requestDto) {
        PillScheduleResponseDto responseDto = pillScheduleService.createSchedule(protectorId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/schedule/{wardId}")
    public ResponseEntity<List<PillScheduleResponseDto>> getScheduleByWard(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable("wardId") Long wardId) {
        List<PillScheduleResponseDto> responseList = pillScheduleService.getSchedulesByWard(protectorId, wardId);
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/schedule/{id}")
    public ResponseEntity<PillScheduleResponseDto> updateSchedule(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable("id") Long id,
            @RequestBody  PillScheduleRequestDto requestDto) {
        PillScheduleResponseDto responseDto = pillScheduleService.updateSchedule(protectorId, id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/schedule/{id}/toggle")
    public ResponseEntity<PillScheduleResponseDto> toggleAlarmStatus(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable("id") Long id,
            @RequestParam boolean isActive) {
        PillScheduleResponseDto responseDto = pillScheduleService.toggleAlarmStatus(protectorId, id, isActive);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("schedule/{id}")
    public ResponseEntity<PillScheduleResponseDto> deleteSchedule(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable("id") Long id) {
        pillScheduleService.deleteSchedule(protectorId, id);
        return ResponseEntity.noContent().build();
    }
}
