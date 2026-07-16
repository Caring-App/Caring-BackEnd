package com.caring.domain.notification.dto;

import com.caring.domain.notification.entity.NotificationLog;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationLogResponseDto {
    private final Long notificationId;
    private final String title;
    private final String content;

    @JsonProperty("isRead")
    private final boolean isRead;

    private final LocalDateTime createdAt;

    public NotificationLogResponseDto(NotificationLog entity) {
        this.notificationId = entity.getNotificationId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.isRead = entity.isRead();
        this.createdAt = entity.getCreatedAt();
    }
}
