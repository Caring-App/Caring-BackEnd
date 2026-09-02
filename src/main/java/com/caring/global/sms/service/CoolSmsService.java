package com.caring.global.sms.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoolSmsService {
    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.sender}")
    private String sender;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        log.info("[CoolSmsService] 초기화 완료");
    }

    public void sendSms(String to, String content) {
        Message message = new Message();
        message.setFrom(sender);
        message.setTo(to);
        message.setText(content);

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("[SMS 발송 성공] to: {}", to);
        } catch(Exception e) {
            log.error("[SMS 발송 실패] to: {}, error: {}", to, e.getMessage());
            throw new IllegalArgumentException("SMS 발송에 실패했습니다.", e);
        }
    }
}
