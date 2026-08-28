package com.caring.global.geocoding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoGeocodingService {

    private final RestClient restClient;

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    public Optional<Coordinate> geocode(String address) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://dapi.kakao.com/v2/local/search/address.json?query={address}", address)
                    .header("Authorization", "KakaoAK " + restApiKey)
                    .retrieve()
                    .body(Map.class);

            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
            if (documents == null || documents.isEmpty()) {
                log.warn("[Geocoding 실패] 주소 매칭 결과 없음: {}", address);
                return Optional.empty();
            }

            Map<String, Object> first = documents.get(0);
            double lat = Double.parseDouble((String) first.get("y"));
            double lng = Double.parseDouble((String) first.get("x"));

            return Optional.of(new Coordinate(lat, lng));
        } catch (Exception e) {
            log.error("[Geocoding 에러] 주소: {}, 에러: {}", address, e.getMessage());
            return Optional.empty();
        }
    }
}