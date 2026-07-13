package com.caring.domain.location.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.location.dto.LocationRequestDto;
import com.caring.domain.location.dto.LocationResponseDto;
import com.caring.domain.location.entity.LocationLog;
import com.caring.domain.location.entity.Place;
import com.caring.domain.location.repository.LocationRepository;
import com.caring.domain.location.repository.PlaceRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.global.common.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private static final double VISIT_RADIUS_METERS = 100.0;
    private static final int MIN_STAY_SECONDS = 5 * 60;
    private static final int LOOKBACK_MINUTES = 30;

    private final LocationRepository locationRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;

    @Transactional
    public LocationResponseDto saveLocation(Long wardId, LocationRequestDto requestDto) {
        Member ward = memberRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자가 존재하지 않습니다. ID = " + wardId));

        LocalDateTime now = LocalDateTime.now();

        // 1. 등록된 장소들 중 지금 위치와 100m 이내인 곳이 있는지 확인
        List<Place> places = placeRepository.findByWard_MemberId(wardId);
        Place matchedPlace = places.stream()
                .filter(place -> GeoUtils.calculateDistance(
                        requestDto.getLatitude(), requestDto.getLongitude(),
                        place.getLatitude(), place.getLongitude()) <= VISIT_RADIUS_METERS)
                .findFirst()
                .orElse(null);

        int stayDuration = 0;
        boolean isVisitVerified = false;

        if (matchedPlace != null) {
            // 2. 최근 기록들을 거슬러 올라가며, 같은 장소 반경 안에 계속 있었던 시작 시점 찾기
            List<LocationLog> recentLogs = locationRepository
                    .findByWard_MemberIdAndRecordedAtAfterOrderByRecordedAtDesc(wardId, now.minusMinutes(LOOKBACK_MINUTES));

            LocalDateTime stayStartTime = now;
            for (LocationLog log : recentLogs) {
                double distance = GeoUtils.calculateDistance(
                        log.getLatitude(), log.getLongitude(),
                        matchedPlace.getLatitude(), matchedPlace.getLongitude());

                if (distance <= VISIT_RADIUS_METERS) {
                    stayStartTime = log.getRecordedAt();
                } else {
                    break;   // 반경 벗어난 기록이 나오면 거기서 연속 체류가 끊긴 것
                }
            }

            stayDuration = (int) Duration.between(stayStartTime, now).toSeconds();
            isVisitVerified = stayDuration >= MIN_STAY_SECONDS;
        }

        LocationLog locationLog = LocationLog.builder()
                .ward(ward)
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .stayDuration(stayDuration)
                .isVisitVerified(isVisitVerified)
                .recordedAt(now)
                .build();

        return new LocationResponseDto(locationRepository.save(locationLog));
    }


    // 보호자가 ward의 최신 위치 조회 (화면의 지도용)
    public LocationResponseDto getLatestLocation(Long protectorId, Long wardId) {
        boolean isConnected = connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId);
        if (!isConnected) {
            throw new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다.");
        }

        LocationLog latest = locationRepository.findFirstByWard_MemberIdOrderByRecordedAtDesc(wardId);
        if (latest == null) {
            throw new IllegalArgumentException("아직 수신된 위치 정보가 없습니다.");
        }

        return new LocationResponseDto(latest);
    }
}
