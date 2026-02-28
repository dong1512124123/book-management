package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Notification;
import com.example.bookmanagement.repository.AdminRepository;
import com.example.bookmanagement.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// 알림 관련 비즈니스 로직 담당
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AdminRepository adminRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               AdminRepository adminRepository) {
        this.notificationRepository = notificationRepository;
        this.adminRepository = adminRepository;
    }

    // 알림 생성
    @Transactional
    public Notification createNotification(String recipientType, Long recipientId,
                                            String type, String title, String message) {
        Notification notification = new Notification();
        notification.setRecipientType(recipientType);
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    // 모든 관리자에게 알림 전송
    @Transactional
    public void notifyAllAdmins(String type, String title, String message) {
        adminRepository.findAll().forEach(admin -> {
            createNotification("ADMIN", admin.getId(), type, title, message);
        });
    }

    // 특정 사용자에게 알림 전송
    @Transactional
    public void notifyUser(Long memberId, String type, String title, String message) {
        createNotification("USER", memberId, type, title, message);
    }

    // 특정 수신자의 알림 목록 조회
    public List<Notification> getNotifications(String recipientType, Long recipientId) {
        return notificationRepository
                .findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(recipientType, recipientId);
    }

    // 읽지 않은 알림 수
    public long countUnread(String recipientType, Long recipientId) {
        return notificationRepository
                .countByRecipientTypeAndRecipientIdAndIsReadFalse(recipientType, recipientId);
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(String recipientType, Long recipientId) {
        List<Notification> unreadList = notificationRepository
                .findByRecipientTypeAndRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientType, recipientId);
        for (Notification n : unreadList) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }
}
