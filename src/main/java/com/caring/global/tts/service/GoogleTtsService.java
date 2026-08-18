package com.caring.global.tts.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class GoogleTtsService {
    private TextToSpeechClient client;

    @PostConstruct
    public void init() throws Exception {
        try(InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(com.google.api.gax.core.FixedCredentialsProvider.create(credentials))
                    .build();

            this.client = TextToSpeechClient.create(settings);
            log.info("[GoogleTtsService] TextToSpeechClient 초기화 완료");
        }
    }

    @PreDestroy
    public void shutdown() {
        if(client != null) {
            client.close();
        }
    }

    public byte[] synthesize(String text, double speakingRate) {
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("ko-KR")
                .setName("ko-KR-Neural2-A")
                .build();

        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .setSpeakingRate(speakingRate)
                .build();

        try {
            SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();

            log.info("[TTS 합성 완료] 텍스트 길이: {}자, 속도: {}", text.length(), speakingRate);
            return audioContents.toByteArray();
        } catch (Exception e) {
            log.error("[TTS 합성 실패] 에러: {}", e.getMessage());
            throw new IllegalStateException("TTS 음성 합성에 실패했습니다.", e);
        }
    }
}
