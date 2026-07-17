package com.caring.domain.health.controller;

import com.caring.domain.health.dto.HealthRecordRequestDto;
import com.caring.domain.health.dto.HealthRecordResponseDto;
import com.caring.domain.health.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-record")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    /*
     * 돌봄대상자 본인이 특정 기저질환에 대한 오늘의 수치 입력
     * POST http://localhost:8080/api/health-record
     */
    @PostMapping
    public ResponseEntity<Void> recordHealth(
            @AuthenticationPrincipal Long wardId,
            @RequestBody HealthRecordRequestDto requestDto){

        healthRecordService.recordHealth(wardId,requestDto.getDiseaseId(), requestDto.getHealthValue());
        return ResponseEntity.ok().build();
    }

    /*
     * 보호자가 오늘 기록 조회
     * GET http://localhost:8080/api/health-record/{wardId}
     */
    @GetMapping("/{wardId}")
    public ResponseEntity<List<HealthRecordResponseDto>> getTodayHealthRecords(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {

        return ResponseEntity.ok(healthRecordService.getTodayHealthRecords(protectorId, wardId));
    }
}
