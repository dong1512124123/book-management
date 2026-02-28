package com.example.bookmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// @Entity: 이 클래스가 DB의 'book' 테이블과 1:1 매핑됨
// 대출 관련 정보는 Loan 엔티티에서 관리 (책임 분리)
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 도서 제목

    @Column(nullable = false, length = 100)
    private String author; // 저자

    @Column(nullable = false, length = 100)
    private String publisher; // 출판사

    @Column(nullable = false, length = 20)
    private String isbn; // ISBN 번호

    @Column(columnDefinition = "TEXT")
    private String description; // 도서 개요

    @Column(length = 255)
    private String coverImage; // 표지 이미지 파일명

    @Column(updatable = false)
    private LocalDateTime createdAt; // 등록일시

    // 기본 생성자 (JPA 필수)
    public Book() {
    }

    public Book(String title, String author, String publisher, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}
