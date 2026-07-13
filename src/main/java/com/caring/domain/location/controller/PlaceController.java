package com.caring.domain.location.controller;

import com.caring.domain.location.dto.PlaceRequestDto;
import com.caring.domain.location.dto.PlaceResponseDto;
import com.caring.domain.location.entity.Place;
import com.caring.domain.location.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceResponseDto> createPlace(
            @AuthenticationPrincipal Long wardId,
            @RequestBody PlaceRequestDto requestDto) {
        return ResponseEntity.ok(placeService.createPlace(wardId, requestDto));
    }

    @GetMapping("/{wardId}")
    public ResponseEntity<List<PlaceResponseDto>> getPlaces(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long wardId) {
        return ResponseEntity.ok(placeService.getPlaces(protectorId, wardId));
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @AuthenticationPrincipal Long protectorId,
            @PathVariable Long placeId) {
        placeService.deletePlace(protectorId, placeId);
        return ResponseEntity.noContent().build();
    }
}
