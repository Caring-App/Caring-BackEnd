package com.caring.domain.welfarefacility.service;

import com.caring.domain.member.entity.Member;
import com.caring.domain.welfarefacility.dto.WelfareFacilityResponseDto;
import com.caring.domain.welfarefacility.entity.WelfareFacility;
import com.caring.domain.welfarefacility.repository.WelfareFacilityRepository;
import com.caring.global.geocoding.service.Coordinate;
import com.caring.global.geocoding.service.KakaoGeocodingService;
import com.caring.global.common.GeoUtils;
import com.caring.global.welfareapi.WelfareFacilityApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelfareFacilityService {

    private final WelfareFacilityApiClient apiClient;
    private final WelfareFacilityRepository welfareFacilityRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

    private static final Map<String, String> SIDO_FULL_NAME = Map.ofEntries(
            Map.entry("서울", "서울특별시"),
            Map.entry("부산", "부산광역시"),
            Map.entry("대구", "대구광역시"),
            Map.entry("인천", "인천광역시"),
            Map.entry("광주", "광주광역시"),
            Map.entry("대전", "대전광역시"),
            Map.entry("울산", "울산광역시"),
            Map.entry("세종", "세종특별자치시"),
            Map.entry("경기", "경기도"),
            Map.entry("강원", "강원특별자치도"),
            Map.entry("충북", "충청북도"),
            Map.entry("충남", "충청남도"),
            Map.entry("전북", "전북특별자치도"),
            Map.entry("전남", "전라남도"),
            Map.entry("경북", "경상북도"),
            Map.entry("경남", "경상남도"),
            Map.entry("제주", "제주특별자치도")
    );

    @Transactional
    public List<WelfareFacilityResponseDto> getNearbyFacilities(Member ward, double radiusKm) {
        if (ward.getLatitude() == null || ward.getLongitude() == null) {
            throw new IllegalStateException("대상자의 좌표 정보가 없어 근처 시설을 조회할 수 없습니다.");
        }

        String sigunguNm = extractSigungu(ward.getBaseAddress());

        List<Map<String, Object>> rawList = apiClient.getFacilityList(sigunguNm);
        if (rawList.isEmpty()) {
            return List.of();
        }
        Long jrsdSggCd = ((Number) rawList.get(0).get("jrsdSggCd")).longValue();

        List<Map<String, Object>> detailList = apiClient.getFacilityDetailsBySigungu(jrsdSggCd);

        // 1단계: 순차로 캐시 여부 확인
        List<Map<String, Object>> uncachedDetails = detailList.stream()
                .filter(detail -> {
                    String fcltCd = (String) detail.get("fcltCd");
                    return fcltCd != null && welfareFacilityRepository.findByFcltCd(fcltCd).isEmpty();
                })
                .toList();

        // 2단계: 캐시 없는 것들만 geocoding 병렬 처리
        Map<String, Coordinate> geocodedMap = new ConcurrentHashMap<>();
        uncachedDetails.parallelStream()
                .forEach(detail -> {
                    String fcltCd = (String) detail.get("fcltCd");
                    String address = toStringOrNull(detail.get("fcltAddr"));
                    if (address == null) {
                        return;
                    }
                    kakaoGeocodingService.geocode(address).ifPresent(coordinate ->
                            geocodedMap.put(fcltCd, coordinate)
                    );
                });

        // 3단계: 순차로 DB 저장/조회
        List<WelfareFacility> facilities = detailList.stream()
                .map(detail -> getOrCacheFacilityFromDetail(detail, geocodedMap))
                .filter(Objects::nonNull)
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .toList();

        return facilities.stream()
                .map(f -> {
                    double distanceKm = GeoUtils.calculateDistance(
                            ward.getLatitude(), ward.getLongitude(), f.getLatitude(), f.getLongitude()) / 1000.0;
                    return new WelfareFacilityResponseDto(f, distanceKm);
                })
                .filter(dto -> dto.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(WelfareFacilityResponseDto::getDistanceKm))
                .toList();
    }

    private WelfareFacility getOrCacheFacilityFromDetail(Map<String, Object> detail, Map<String, Coordinate> geocodedMap) {
        String fcltCd = (String) detail.get("fcltCd");
        if (fcltCd == null) {
            return null;
        }

        return welfareFacilityRepository.findByFcltCd(fcltCd)
                .orElseGet(() -> {
                    Coordinate coordinate = geocodedMap.get(fcltCd);
                    if (coordinate == null) {
                        log.warn("[시설 좌표 변환 실패] fcltCd: {}", fcltCd);
                        return null;
                    }

                    String address = toStringOrNull(detail.get("fcltAddr"));
                    String detailAddr = toStringOrNull(detail.get("fcltDtl_1Addr"));
                    String fullAddress = address + (detailAddr != null ? " " + detailAddr : "");

                    WelfareFacility newFacility = WelfareFacility.builder()
                            .fcltCd(fcltCd)
                            .fcltNm(toStringOrNull(detail.get("fcltNm")))
                            .address(fullAddress)
                            .latitude(coordinate.latitude())
                            .longitude(coordinate.longitude())
                            .telNo(toStringOrNull(detail.get("fcltTelNo")))
                            .cprNm(toStringOrNull(detail.get("cprNm")))
                            .homepageAddr(toStringOrNull(detail.get("homepageAddr")))
                            .build();

                    return welfareFacilityRepository.save(newFacility);
                });
    }

    private String toStringOrNull(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String extractSigungu(String baseAddress) {
        String[] parts = baseAddress.trim().split("\\s+");
        if (parts.length < 2) {
            return baseAddress;
        }
        String sido = SIDO_FULL_NAME.getOrDefault(parts[0], parts[0]);
        String result = sido + " " + parts[1];
        log.info("[시군구 추출] baseAddress: {}, 추출결과: {}", baseAddress, result);
        return result;
    }
}