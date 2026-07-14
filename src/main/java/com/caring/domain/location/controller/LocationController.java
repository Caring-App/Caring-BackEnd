package com.caring.domain.location.controller;

import com.caring.domain.location.dto.LocationRequestDto;
import com.caring.domain.location.dto.LocationResponseDto;
import com.caring.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponseDto> saveLocation(
            @AuthenticationPrincipal Long wardId,
            @RequestBody LocationRequestDto requestDto
    ) {
        return ResponseEntity.ok(locationService.saveLocation(wardId, requestDto));
    }

    @GetMapping("/{wardId}/latest")
    public ResponseEntity<LocationResponseDto> getLatestLocation(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId
    ) {
        return ResponseEntity.ok(locationService.getLatestLocation(protectorId, wardId));
    }
}
