package com.example.bookmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// @Entity: 이 클래스가 DB의 'member' 테이블과 1:1 매핑됨
// 회원 정보를 저장하는 엔티티
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // 회원 이름

    @Column(nullable = false, length = 20)
    private String phone; // 회원 전화번호

    @Column(unique = true, length = 50)
    private String username; // 로그인 아이디

    @Column(length = 200)
    private String password; // 암호화된 비밀번호

    @Column(length = 20)
    private String role; // 역할: "USER"

    @Column(updatable = false)
    private LocalDateTime createdAt; // 등록일시

    // 기본 생성자 (JPA 필수)
    public Member() {
    }

    public Member(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.role = "USER";
        this.createdAt = LocalDateTime.now();
    }

    public Member(String name, String phone, String username, String password) {
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.role = "USER";
        this.createdAt = LocalDateTime.now();
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
