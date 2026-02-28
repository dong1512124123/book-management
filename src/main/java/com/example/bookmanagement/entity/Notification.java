package com.example.bookmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 알림 엔티티
// 대출 승인/반납 등 상태 변경 시 상대방에게 전달되는 알림
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수신자 유형: "ADMIN" 또는 "USER"
    @Column(nullable = false, length = 10)
    private String recipientType;

    // 수신자 ID (Admin 또는 Member의 ID)
    @Column(nullable = false)
    private Long recipientId;

    // 알림 유형: LOAN_REQUESTED, LOAN_APPROVED, LOAN_REJECTED, RETURN_REQUESTED, RETURN_CONFIRMED
    @Column(nullable = false, length = 30)
    private String type;

    // 알림 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 알림 내용
    @Column(nullable = false, length = 500)
    private String message;

    // 읽음 여부
    @Column(nullable = false)
    private boolean isRead = false;

    // 생성 시각
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }

    // --- Getter/Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
