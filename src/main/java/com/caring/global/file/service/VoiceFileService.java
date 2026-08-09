package com.caring.global.file.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
}
