package com.example.bookmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

// @Entity: 이 클래스가 DB의 'loan' 테이블과 1:1 매핑됨
// 대출/반납 기록을 저장하는 엔티티
// Book과 Member를 연결하는 "다리" 역할
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne: 여러 대출 기록이 하나의 도서를 참조할 수 있음
    // (한 도서가 시간 차를 두고 여러 번 대출될 수 있으므로)
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // @ManyToOne: 여러 대출 기록이 하나의 회원을 참조할 수 있음
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate borrowDate; // 대출일

    @Column(nullable = false)
    private LocalDate returnDueDate; // 반납 예정일

    private LocalDate returnedDate; // 실제 반납일 (null이면 아직 대출 중)

    // 대출 상태: REQUESTED(신청) → APPROVED(승인) → RETURN_REQUESTED(반납신청) → RETURNED(반납완료)
    @Column(length = 30)
    private String status;

    @Column(updatable = false)
    private LocalDateTime createdAt; // 기록 생성일시

    // 기본 생성자 (JPA 필수)
    public Loan() {
    }

    // 대출 중인지 확인 (승인 상태이고 반납 안 됨)
    public boolean isActive() {
        return "APPROVED".equals(this.status) && this.returnedDate == null;
    }

    // 연체 여부 확인
    public boolean isOverdue() {
        return isActive() && LocalDate.now().isAfter(this.returnDueDate);
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getReturnDueDate() { return returnDueDate; }
    public void setReturnDueDate(LocalDate returnDueDate) { this.returnDueDate = returnDueDate; }

    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
