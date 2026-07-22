package com.caring.domain.health.controller;

import com.caring.domain.health.dto.StepRecordRequestDto;
import com.caring.domain.health.dto.StepRecordResponseDto;
import com.caring.domain.health.service.StepRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/step-record")
@RequiredArgsConstructor
public class StepRecordController {

    private final StepRecordService stepRecordService;

    /*
     * 돌봄대상자 본인이 오늘의 걸음 수 입력
     * POST http://localhost:8080/api/step-record
     */
    @PostMapping
    public ResponseEntity<Void> recordSteps(
            @AuthenticationPrincipal Long wardId,
            @RequestBody StepRecordRequestDto requestDto) {

        stepRecordService.recordSteps(wardId, requestDto.getSteps());
        return ResponseEntity.ok().build();
    }

    /*
     * 보호자가 오늘 기록 조회
     * GET http://localhost:8080/api/step-record/{wardId}
     */
    @GetMapping("/{wardId}")
    public ResponseEntity<StepRecordResponseDto> getTodaySteps(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {

        return ResponseEntity.ok(stepRecordService.getTodaySteps(protectorId, wardId));
    }
}