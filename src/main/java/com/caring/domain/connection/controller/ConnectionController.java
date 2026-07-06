package com.caring.domain.connection.controller;

import com.caring.domain.connection.dto.ConnectionRequestDto;
import com.caring.domain.connection.dto.ConnectionResponseDto;
import com.caring.domain.connection.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
