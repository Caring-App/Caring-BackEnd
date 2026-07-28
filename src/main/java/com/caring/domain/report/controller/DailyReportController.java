package com.caring.domain.report.controller;

import com.caring.domain.report.dto.DailyReportResponseDto;
import com.caring.domain.report.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily-report")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    /*
     * 보호자가 대상자의 오늘자 리포트 조회
     * GET http://localhost:8080/api/daily-report/{wardId}/today
     */
    @GetMapping("/{wardId}/today")
    public ResponseEntity<DailyReportResponseDto> getTodayReport(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {

        return ResponseEntity.ok(dailyReportService.getTodayReport(protectorId,wardId));
    }
}
