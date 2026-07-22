package com.caring.domain.notification.service;

import com.caring.domain.member.entity.Member;
import com.caring.domain.notification.dto.NotificationLogResponseDto;
import com.caring.domain.notification.entity.NotificationLog;
import com.caring.domain.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationLogService {
    private final NotificationLogRepository notificationLogRepository;

    @Transactional
    public void saveLog(Member receiver, String title, String content) {
        NotificationLog notificationLog = NotificationLog.builder()
                .receiver(receiver)
                .title(title)
                .content(content)
                .build();
        notificationLogRepository.save(notificationLog);
    }


    public List<NotificationLogResponseDto> getNotifications(Long receiverId) {
        return notificationLogRepository.findByReceiver_MemberIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(NotificationLogResponseDto::new)
                .toList();
    }


    @Transactional
    public void markAsRead(Long receiverId, Long notificationId) {
        NotificationLog notificationLog = notificationLogRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        if(!notificationLog.getReceiver().getMemberId().equals(receiverId)) {
            throw new IllegalArgumentException("본인의 알림만 확인할 수 있습니다.");
        }

        notificationLog.markAsRead();
    }
}
