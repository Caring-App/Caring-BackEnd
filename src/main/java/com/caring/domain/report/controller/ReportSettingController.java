package com.caring.domain.report.controller;

import com.caring.domain.report.dto.ReportTimeResponseDto;
import com.caring.domain.report.dto.ReportTimeUpdateRequestDto;
import com.caring.domain.report.service.ReportSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report-setting")
public class ReportSettingController {

    private final ReportSettingService reportSettingService;

    /*
     * 레포트 시간 조회
     * GET http://localhost:8080/api/report-setting/{wardId}
     */
    @GetMapping("/{wardId}")
    public ResponseEntity<ReportTimeResponseDto> getReportTime(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {

        ReportTimeResponseDto response = ReportTimeResponseDto.builder()
                .reportTime(reportSettingService.getEffectiveReportTime(protectorId, wardId))
                .build();
        return ResponseEntity.ok(response);
    }


    /*
     * 레포트 시간 변경
     * PATCH http://localhost:8080/api/report-setting/{wardId}
     */
    @PatchMapping("/{wardId}")
    public ResponseEntity<Void> updateReportTime(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId,
            @RequestBody ReportTimeUpdateRequestDto requestDto) {

        reportSettingService.updateReportTime(protectorId, wardId, requestDto.getReportTime());
        return ResponseEntity.ok().build();
    }
}