package com.caring.domain.pill.controller;

import com.caring.domain.pill.dto.PillConfirmRequestDto;
import com.caring.domain.pill.dto.PillLogResponseDto;
import com.caring.domain.pill.service.PillLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<PillLogResponseDto> confirmPill(@RequestBody PillConfirmRequestDto requestDto) {
        // pillLogService.confirmPill() 호출
        PillLogResponseDto responseDto = pillLogService.confirmPill(requestDto.getPillLogId());

        return ResponseEntity.ok().body(responseDto);
    }
}
