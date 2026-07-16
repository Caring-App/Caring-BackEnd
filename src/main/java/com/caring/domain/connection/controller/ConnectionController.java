package com.caring.domain.connection.controller;

import com.caring.domain.connection.dto.*;
import com.caring.domain.connection.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/connection")
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping
    public ResponseEntity<ConnectionResponseDto> connect(
            @AuthenticationPrincipal Long wardId,
            @RequestBody ConnectionRequestDto requestDto){

        // 서비스에게 넘겨서 보호자, 돌봄대상자 연결 처리
        ConnectionResponseDto response = connectionService.connect(wardId,requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WardSummaryResponseDto>> getConnectedWards(
            @AuthenticationPrincipal Long protectorId) {
        return ResponseEntity.ok(connectionService.getConnectedWards(protectorId));
    }

    @GetMapping("/{wardId}")
    public ResponseEntity<WardDetailResponseDto> getWardDetail(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {
        return ResponseEntity.ok(connectionService.getWardDetail(protectorId,wardId));
    }

    @PatchMapping("/{wardId}")
    public ResponseEntity<WardDetailResponseDto> updateWard(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId,
            @RequestBody WardUpdateRequestDto requestDto) {
        return ResponseEntity.ok(connectionService.updateWard(protectorId,wardId,requestDto));
    }
}
