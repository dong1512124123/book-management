package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// 대출 기록 DB 접근 담당
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 현재 대출 중인 기록 (반납일이 null = 아직 반납 안 함)
    List<Loan> findByReturnedDateIsNull();

    // 반납 완료된 기록
    List<Loan> findByReturnedDateIsNotNull();

    // 특정 도서의 활성 대출 기록 (대출 중인지 확인용)
    Optional<Loan> findByBookIdAndReturnedDateIsNull(Long bookId);

    // 현재 대출 중인 건수
    long countByReturnedDateIsNull();

    // 특정 회원의 대출 기록
    List<Loan> findByMemberId(Long memberId);

    // 특정 회원의 현재 대출 중인 기록
    List<Loan> findByMemberIdAndReturnedDateIsNull(Long memberId);

    // --- 상태(status)별 조회 ---
    // 특정 상태의 대출 목록
    List<Loan> findByStatus(String status);

    // 특정 상태 건수
    long countByStatus(String status);

    // 특정 도서의 활성 대출 (승인됨 상태)
    Optional<Loan> findByBookIdAndStatus(Long bookId, String status);

    // 특정 회원의 대출 기록 (상태 포함)
    List<Loan> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 특정 도서의 전체 대출 이력 (최신순)
    List<Loan> findByBookIdOrderByCreatedAtDesc(Long bookId);

    // 특정 회원의 대출 기록 전체 삭제
    void deleteByMemberId(Long memberId);

    // 특정 도서의 대출 기록 전체 삭제
    void deleteByBookId(Long bookId);
}
