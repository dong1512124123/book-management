package com.example.bookmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// 관리자 엔티티
// 관리자 로그인, 도서 관리, 대출 승인 등의 권한을 가짐
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 로그인 아이디

    @Column(nullable = false, length = 200)
    private String password; // 암호화된 비밀번호

    @Column(length = 50)
    private String name; // 관리자 이름 (최초 로그인 시 입력)

    @Column(length = 20)
    private String phone; // 관리자 전화번호 (비밀번호 찾기용)

    @Column(nullable = false)
    private boolean firstLogin; // 최초 로그인 여부 (true면 정보 입력 필요)

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 기본 생성자 (JPA 필수)
    public Admin() {
    }

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.firstLogin = true;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isFirstLogin() { return firstLogin; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
