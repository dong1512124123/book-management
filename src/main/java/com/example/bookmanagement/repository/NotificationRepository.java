package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 알림 DB 접근 담당
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 수신자의 알림 목록 (최신순)
    List<Notification> findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(String recipientType, Long recipientId);

    // 특정 수신자의 읽지 않은 알림 수
    long countByRecipientTypeAndRecipientIdAndIsReadFalse(String recipientType, Long recipientId);

    // 특정 수신자의 읽지 않은 알림 목록
    List<Notification> findByRecipientTypeAndRecipientIdAndIsReadFalseOrderByCreatedAtDesc(String recipientType, Long recipientId);
}
