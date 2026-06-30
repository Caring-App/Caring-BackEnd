package com.caring.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j // 로그 남기는 도구, 알림 내역 확인할 때 사용
public class FcmService {
    // 푸시 알림 기능 서비스, 누구한테 보내고, 제목과 내용 설정

    // 푸시 알림 보내는 메소드
    public void sendNotification(String fcmToken, String title, String body){

        // 1. 메시지 만들기
        Message message = Message.builder() // fcm 메세지 객체
                .setToken(fcmToken) // 누구에게 보낼지
                .setNotification(Notification.builder()
                        .setTitle(title) // 알림 제목
                        .setBody(body) // 알림 내용
                        .build())
                .build();

        // 2. 메시지 전송
        try {
            String response = FirebaseMessaging.getInstance().send(message); // firebase에 메시지 전송
            log.info("FCM 알림 전송 성공 : {}", response); // 성공 로그 남기기
        } catch (FirebaseMessagingException e){
            log.error("FCM 알림 전송 실패 : {}", e.getMessage()); // 실패 시 에러 로그 남기기
        }

    }
}
