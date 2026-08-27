package com.caring.domain.pill.controller;

import com.caring.domain.pill.dto.PillConfirmRequestDto;
import com.caring.domain.pill.dto.PillLogResponseDto;
import com.caring.domain.pill.dto.PillTodayResponseDto;
import com.caring.domain.pill.service.PillLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pill")
@RequiredArgsConstructor
public class PillLogController {

    private final PillLogService pillLogService;

    /*
     * 복약 확인 응답 처리
     * POST http://localhost:8080/api/pill/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<PillLogResponseDto> confirmPill(
            @AuthenticationPrincipal Long wardId,
            @RequestBody PillConfirmRequestDto requestDto) {
        // pillLogService.confirmPill() 호출
        PillLogResponseDto responseDto = pillLogService.confirmPill(wardId, requestDto.getPillLogId());

        return ResponseEntity.ok().body(responseDto);
    }

    /*
     * 오늘자 복약 목록 + 복용 상태 조회
     * GET http://localhost:8080/api/pill/today
     */
    @GetMapping("/today")
    public ResponseEntity<List<PillTodayResponseDto>> getTodayPillStatus(
            @AuthenticationPrincipal Long wardId) {
        List<PillTodayResponseDto> responseDto = pillLogService.getTodayPillStatus(wardId);

        return ResponseEntity.ok().body(responseDto);
    }

}
