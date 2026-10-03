package com.caring.global.file.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceFileService {
    private final Bucket firebaseStorageBucket;

    public String uploadVoiceFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename !=  null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = "voice/" + UUID.randomUUID() + extension;

        Blob blob = firebaseStorageBucket.create(fileName, file.getBytes(), file.getContentType());

        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://storage.googleapis.com/%s/%s",
                firebaseStorageBucket.getName(), fileName);
    }

    public void deleteVoiceFile(String voiceFileUrl) {
        if(voiceFileUrl == null || voiceFileUrl.isBlank()) {
            return ;
        }

        String prefix = String.format("https://storage.googleapis.com/%s/", firebaseStorageBucket.getName());
        if(!voiceFileUrl.startsWith(prefix)) {
            log.warn("[음성 파일 삭제 실패] Storage 버킷 URL 형식이 아닙니다: {}", voiceFileUrl);
            return;
        }

        String objectName = voiceFileUrl.substring(prefix.length());
        Blob blob = firebaseStorageBucket.get(objectName);
        if (blob != null) {
            blob.delete();
            log.info("[음성 파일 삭제 완료] {}", objectName);
        }
    }
}
