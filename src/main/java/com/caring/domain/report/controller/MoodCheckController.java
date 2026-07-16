package com.caring.domain.report.controller;

import com.caring.domain.report.dto.MoodCheckRequestDto;
import com.caring.domain.report.dto.MoodCheckResponseDto;
import com.caring.domain.report.service.MoodCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mood-check")
public class MoodCheckController {

    private final MoodCheckService moodCheckService;

    /*
     * 돌봄대상자 본인이 오늘의 기분 상태 기록/수정
     * POST http://localhost:8080/api/mood-check
     */
    @PostMapping
    public ResponseEntity<Void> checkMood(
            @AuthenticationPrincipal Long wardId,
            @RequestBody MoodCheckRequestDto requestDto){

        moodCheckService.checkMood(wardId,requestDto);
        return ResponseEntity.ok().build();
    }

    /*
     * 보호자가 대상자의 오늘 기분 상태 조회
     * GET http://localhost:8080/api/mood-check/{wardId}
     */
    @GetMapping("/{wardId}")
    public ResponseEntity<MoodCheckResponseDto> getTodayMood(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {

        return ResponseEntity.ok(moodCheckService.getTodayMood(protectorId, wardId));
    }
}
