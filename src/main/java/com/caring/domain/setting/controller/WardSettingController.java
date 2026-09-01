package com.caring.domain.setting.controller;

import com.caring.domain.setting.dto.WardSettingRequestDto;
import com.caring.domain.setting.dto.WardSettingResponseDto;
import com.caring.domain.setting.service.WardSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ward-setting")
public class WardSettingController {
    private final WardSettingService wardSettingService;

    @GetMapping("/{wardId}")
    public ResponseEntity<WardSettingResponseDto> getSetting(@AuthenticationPrincipal Long requesterId,
                                                                 @PathVariable Long wardId) {
        return ResponseEntity.ok(wardSettingService.getSetting(requesterId, wardId));
    }

    @PatchMapping("/{wardId}")
    public ResponseEntity<WardSettingResponseDto> updateSetting(@AuthenticationPrincipal Long protectorId,
                                                                @PathVariable Long wardId,
                                                                @Valid @RequestBody WardSettingRequestDto requestDto) {
        return ResponseEntity.ok(wardSettingService.updateSetting(protectorId, wardId, requestDto));
    }
}
