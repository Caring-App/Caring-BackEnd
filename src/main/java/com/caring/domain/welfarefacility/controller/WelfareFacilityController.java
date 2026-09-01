package com.caring.domain.welfarefacility.controller;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.welfarefacility.dto.WelfareFacilityResponseDto;
import com.caring.domain.welfarefacility.service.WelfareFacilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/welfare-facility")
@Slf4j
public class WelfareFacilityController {
    private final WelfareFacilityService welfareFacilityService;
    private final ConnectionRepository  connectionRepository;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<WelfareFacilityResponseDto>> getNearbyFacilities(
            @AuthenticationPrincipal Long protectorId,
            @RequestParam Long wardId,
            @RequestParam(defaultValue = "3.0") double radiusKm) {

        log.info("[컨트롤러 진입] protectorId={}, wardId={}, radiusKm={}", protectorId, wardId, radiusKm);

        if(!connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId)) {
            throw new IllegalArgumentException("본인과 연결된 돌봄대상자가 아닙니다.");
        }

        var ward = memberRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대상자입니다."));

        return ResponseEntity.ok(welfareFacilityService.getNearbyFacilities(ward, radiusKm));
    }
}
