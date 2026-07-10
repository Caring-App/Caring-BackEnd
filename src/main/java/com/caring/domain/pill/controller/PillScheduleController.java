package com.caring.domain.pill.controller;

import com.caring.domain.pill.dto.PillScheduleRequestDto;
import com.caring.domain.pill.dto.PillScheduleResponseDto;
import com.caring.domain.pill.service.PillScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pill")
@RequiredArgsConstructor
public class PillScheduleController {

    private final PillScheduleService pillScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<PillScheduleResponseDto> createSchedule(@RequestBody PillScheduleRequestDto requestDto) {
        PillScheduleResponseDto responseDto = pillScheduleService.createSchedule(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/schedule/{wardId}")
    public ResponseEntity<List<PillScheduleResponseDto>> getScheduleByWard(@PathVariable("wardId") Long wardId) {
        List<PillScheduleResponseDto> responseList = pillScheduleService.getSchedulesByWard(wardId);
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/schedule/{id}")
    public ResponseEntity<PillScheduleResponseDto> updateSchedule(
            @PathVariable("id") Long id,
            @RequestBody  PillScheduleRequestDto requestDto) {
        PillScheduleResponseDto responseDto = pillScheduleService.updateSchedule(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/schedule/{id}/toggle")
    public ResponseEntity<PillScheduleResponseDto> toggleAlarmStatus(
            @PathVariable("id") Long id,
            @RequestParam boolean isActive) {
        PillScheduleResponseDto responseDto = pillScheduleService.toggleAlarmStatus(id, isActive);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("schedule/{id}")
    public ResponseEntity<PillScheduleResponseDto> deleteSchedule(@PathVariable("id") Long id) {
        pillScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}
