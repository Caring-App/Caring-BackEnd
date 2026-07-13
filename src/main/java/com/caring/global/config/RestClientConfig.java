package com.caring.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration // 설정 파일
public class RestClientConfig {

    @Bean // 반환하는 객체를 빈으로 등록
    public RestClient restClient(){
        return RestClient.create(); // 정적 메소드, new 없어도 가능
    }
}
