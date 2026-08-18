package com.caring.global.tts.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtsFileService {
    private final GoogleTtsService googleTtsService;
    private final Bucket firebaseStorageBucket;

    public String synthesizeAndUpload(String text, double speakingRate) {
        byte[] audioBytes = googleTtsService.synthesize(text, speakingRate);

        String fileName = "tts/" + UUID.randomUUID() + ".mp3";
        Blob blob = firebaseStorageBucket.create(fileName, audioBytes, "audio/mpeg");
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        String url = String.format("https://storage.googleapis.com/%s/%s",
                firebaseStorageBucket.getName(), fileName);

        log.info("[TTS 파일 업로드 완료] url: {}", url);
        return url;
    }
}
