package com.caring.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // firebase 초기화 메소드
    // 서버가 FCM으로 알림 보내려면 파이어베이스한테 우리 서버를 인증해야함 !
    @Bean // 스프링이 관리하는 부품으로 등록
    public FirebaseApp  initializeFirebase() throws IOException{ // 파일 읽다 오류 나는 것 방지

        // 인증되어있는 JSON 파일 읽기
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/firebase-service-account.json");

        // Firebase 연결 설정
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount)) // json 파일로 인증 정보 만들기
                .build();

         return FirebaseApp.initializeApp(options); // firebase 초기화!
    }
}
