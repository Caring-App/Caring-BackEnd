package com.caring.global.welfareapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelfareFacilityApiClient {
    private final RestClient restClient;

    @Value("${welfare-api.service-key}")
    private String serviceKey;

    private static final String BASE_URL = "http://apis.data.go.kr/B554287/sclWlfrFcltInfoInqirService1";

    public List<Map<String, Object>> getFacilityList(String sigunguNm) {
        log.info("[목록조회 시작] sigunguNm={}", sigunguNm);

        Map<String, Object> response = restClient.get()
                .uri(BASE_URL + "/getFcltListInfoInqire?serviceKey={key}&numOfRows=10&pageNo=1&jrsdSggNm={sgg}&_type=json",
                        serviceKey, sigunguNm)
                .retrieve()
                .body(Map.class);

        log.info("[목록조회 원본 응답] {}", response);

        return extractItems(response);
    }

    public List<Map<String, Object>> getFacilityDetailsBySigungu(Long jrsdSggCd) {
        log.info("[구 단위 상세조회 요청] jrsdSggCd={}", jrsdSggCd);

        Map<String, Object> response = restClient.get()
                .uri(BASE_URL + "/getFcltByBassInfoInqire?serviceKey={key}&numOfRows=200&pageNo=1&jrsdSggCd={code}&_type=json",
                        serviceKey, jrsdSggCd)
                .retrieve()
                .body(Map.class);

        log.info("[구 단위 상세조회 원본 응답] jrsdSggCd={}, response={}", jrsdSggCd, response);

        return extractItems(response);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItems(Map<String, Object> rawResponse) {
        Map<String, Object> response = (Map<String, Object>) rawResponse.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Object items = body.get("items");
        if (items instanceof String) {
            return List.of();
        }
        Map<String, Object> itemsMap = (Map<String, Object>) items;
        Object item = itemsMap.get("item");
        if (item instanceof List) {
            return (List<Map<String, Object>>) item;
        } else if (item instanceof Map) {
            return List.of((Map<String, Object>) item);
        }
        return List.of();
    }
}