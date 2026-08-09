package com.caring.global.file.controller;

import com.caring.global.file.service.VoiceFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceUploadController {
    private final VoiceFileService voiceFileService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadVoice(
            @AuthenticationPrincipal Long protectorId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String voiceFileUrl = voiceFileService.uploadVoiceFile(file);
        return ResponseEntity.ok(Map.of("voiceFileUrl", voiceFileUrl));
    }

}
