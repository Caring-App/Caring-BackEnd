package com.caring.domain.notification.controller;

import com.caring.domain.notification.dto.NotificationLogResponseDto;
import com.caring.domain.notification.service.NotificationLogService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationLogController {
    private final NotificationLogService notificationLogService;

    @GetMapping
    public ResponseEntity<List<NotificationLogResponseDto>> getNotifications(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(notificationLogService.getNotifications(memberId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long notificationId) {
        notificationLogService.markAsRead(memberId, notificationId);
        return ResponseEntity.noContent().build();
    }

}
