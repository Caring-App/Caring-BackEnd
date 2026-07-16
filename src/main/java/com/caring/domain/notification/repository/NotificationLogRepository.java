package com.caring.domain.notification.repository;

import com.caring.domain.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByReceiver_MemberIdOrderByCreatedAtDesc(Long receiverId);
}
