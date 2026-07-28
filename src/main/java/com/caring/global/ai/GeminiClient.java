package com.caring.global.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    @Value("${gemini.api-key}")
    private String apiKey;

    private static final String MODEL = "gemini-flash-latest";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    /**
     * 프롬프트를 Gemini API로 보내고, 생성된 텍스트만 뽑아서 리턴
     */
    public String generateText(String prompt) {

        // webClien -> 다른 서버에게 요청을 보내는 도구
        WebClient webClient = WebClient.builder().build();

        /** 제미나이 요청 예시
         * {
         *   "contents": [
         *     { "parts": [ { "text": "여기에 프롬프트 내용" } ] }
         *   ]
         * }
         */
        // 요청안에 넣을 내용물이 담긴 바디 생성
        Map<String, Object> requestBody =
                Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        String fullUrl = BASE_URL + MODEL + ":generateContent";

        // TODO 2: WebClient로 POST 요청 보내기
        Map response = webClient.post() // Post 방식으로 전송
               .uri(URI.create(fullUrl)) // 이 주소로 보낸다는 것
                .header("x-goog-api-key", apiKey)
               .bodyValue(requestBody) // 아까 만든 바디를 넣고
               .retrieve() // 진짜 보냄
               .bodyToMono(Map.class)   // 응답이 오면 Map으로 받아서 파싱해달라는 것
               .block();                 // 응답 올 때까지 결과 기다리기 ( 동기 방식 )

        /** 제미나이 응답 JSON
         * {
         "candidates": [                          ← List
         {                                       ← Map
         "content": {                         ← Map
         "parts": [                         ← List
         { "text": "AI가 생성한 텍스트" }  ← Map
         ]
         }
         }
         ]
         }
         */

        // response는 제일 바깥 {} 전체를 담은 Map -> candidates 를 뽑아야함
        List<Map> candidates = (List<Map>) response.get("candidates");

        // 뽑은 candidates 리스트에서 첫번째 요소를 꺼내기 -> map 하나가 나옴
        Map firstCandidate = candidates.get(0);

        // 나온 map에서 contents라는 키 값 찾기 ->  { part ~ 가 나옴
        Map content = (Map) firstCandidate.get("content");

        // part 를 찾아내고 그 리스트에서의 0번 인덱스를 꺼내기
        List<Map> parts = (List<Map>) content.get("parts");
        Map firstPart = parts.get(0);

        // 0번 인덱스 중에 text 이름표를 찾으면 텍스트 문자열이 나옴
        String text = (String) firstPart.get("text");

        return text;
    }
}