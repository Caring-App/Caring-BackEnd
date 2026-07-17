package com.caring.domain.health.controller;

import com.caring.domain.health.dto.HealthRecordRequestDto;
import com.caring.domain.health.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
