package com.example.bookmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 로그인 이력 엔티티
// 누가 언제 로그인했는지 기록
@Entity
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 유형: "ADMIN" 또는 "USER"
    @Column(nullable = false, length = 10)
    private String userType;

    // 사용자 ID
    @Column(nullable = false)
    private Long userId;

    // 로그인 아이디
    @Column(nullable = false, length = 50)
    private String username;

    // 로그인 시각
    @Column(nullable = false)
    private LocalDateTime loginTime;

    // 접속 IP 주소
    @Column(length = 50)
    private String ipAddress;

    public LoginHistory() {
    }

    // --- Getter/Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
