package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 대출/반납 관련 비즈니스 로직 담당
// 상태 흐름: REQUESTED(신청) → APPROVED(승인) → RETURN_REQUESTED(반납신청) → RETURNED(반납완료)
@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    public LoanService(LoanRepository loanRepository, NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
    }

    // 전체 대출 기록 조회
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("대출 기록을 찾을 수 없습니다. ID: " + id));
    }

    // 현재 대출 중인 기록만 조회 (승인됨 상태)
    public List<Loan> findActiveLoans() {
        return loanRepository.findByStatus("APPROVED");
    }

    // 현재 대출 중인 건수
    public long countActiveLoans() {
        return loanRepository.countByStatus("APPROVED");
    }

    // 승인 대기 중인 건수
    public long countPendingRequests() {
        return loanRepository.countByStatus("REQUESTED");
    }

    // 반납 대기 중인 건수
    public long countReturnRequests() {
        return loanRepository.countByStatus("RETURN_REQUESTED");
    }

    // 상태별 대출 목록 조회
    public List<Loan> findByStatus(String status) {
        return loanRepository.findByStatus(status);
    }

    // 현재 대출 중인 도서 ID 목록
    public Set<Long> getBorrowedBookIds() {
        Set<Long> ids = new HashSet<>();
        // 신청 중이거나 승인된 도서도 포함
        for (Loan loan : loanRepository.findByStatus("REQUESTED")) {
            ids.add(loan.getBook().getId());
        }
        for (Loan loan : loanRepository.findByStatus("APPROVED")) {
            ids.add(loan.getBook().getId());
        }
        for (Loan loan : loanRepository.findByStatus("RETURN_REQUESTED")) {
            ids.add(loan.getBook().getId());
        }
        return ids;
    }

    // 도서가 현재 대출 중인지 확인
    public boolean isBookBorrowed(Long bookId) {
        return loanRepository.findByBookIdAndReturnedDateIsNull(bookId).isPresent();
    }

    // 특정 도서의 활성 대출 기록 조회 (대여자 정보 표시용)
    public Loan findActiveLoanByBookId(Long bookId) {
        return loanRepository.findByBookIdAndReturnedDateIsNull(bookId).orElse(null);
    }

    // 사용자 → 대출 신청 (관리자 승인 대기)
    @Transactional
    public Loan requestLoan(Book book, Member member) {
        if (isBookBorrowed(book.getId())) {
            throw new RuntimeException("이미 대출 중인 도서입니다: " + book.getTitle());
        }
        // 이미 신청 중인지 확인
        if (loanRepository.findByBookIdAndStatus(book.getId(), "REQUESTED").isPresent()) {
            throw new RuntimeException("이미 대출 신청 중인 도서입니다: " + book.getTitle());
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setReturnDueDate(LocalDate.now().plusDays(14));
        loan.setStatus("REQUESTED");
        loan.setCreatedAt(LocalDateTime.now());
        Loan saved = loanRepository.save(loan);

        // 알림: 모든 관리자에게 대출 신청 알림
        notificationService.notifyAllAdmins(
                "LOAN_REQUESTED",
                "새 대출 신청",
                member.getName() + "님이 '" + book.getTitle() + "' 도서 대출을 신청했습니다.");
        return saved;
    }

    // 관리자 → 대출 승인
    @Transactional
    public Loan approveLoan(Long loanId) {
        Loan loan = findById(loanId);
        loan.setStatus("APPROVED");
        loan.setBorrowDate(LocalDate.now());
        loan.setReturnDueDate(LocalDate.now().plusDays(14));
        Loan saved = loanRepository.save(loan);

        // 알림: 사용자에게 대출 승인 알림
        notificationService.notifyUser(
                loan.getMember().getId(),
                "LOAN_APPROVED",
                "대출 승인",
                "'" + loan.getBook().getTitle() + "' 도서 대출이 승인되었습니다. 반납기한: " + loan.getReturnDueDate());
        return saved;
    }

    // 관리자 → 대출 거절 (삭제)
    @Transactional
    public void rejectLoan(Long loanId) {
        Loan loan = findById(loanId);
        // 알림: 사용자에게 대출 거절 알림
        notificationService.notifyUser(
                loan.getMember().getId(),
                "LOAN_REJECTED",
                "대출 거절",
                "'" + loan.getBook().getTitle() + "' 도서 대출 신청이 거절되었습니다.");
        loanRepository.deleteById(loanId);
    }

    // 사용자 → 반납 신청
    @Transactional
    public Loan requestReturn(Long loanId) {
        Loan loan = findById(loanId);
        loan.setStatus("RETURN_REQUESTED");
        Loan saved = loanRepository.save(loan);

        // 알림: 모든 관리자에게 반납 요청 알림
        notificationService.notifyAllAdmins(
                "RETURN_REQUESTED",
                "반납 요청",
                loan.getMember().getName() + "님이 '" + loan.getBook().getTitle() + "' 도서 반납을 요청했습니다.");
        return saved;
    }

    // 관리자 → 반납 확인
    @Transactional
    public Loan confirmReturn(Long loanId) {
        Loan loan = findById(loanId);
        loan.setStatus("RETURNED");
        loan.setReturnedDate(LocalDate.now());
        Loan saved = loanRepository.save(loan);

        // 알림: 사용자에게 반납 확인 알림
        notificationService.notifyUser(
                loan.getMember().getId(),
                "RETURN_CONFIRMED",
                "반납 확인",
                "'" + loan.getBook().getTitle() + "' 도서 반납이 확인되었습니다.");
        return saved;
    }

    // 관리자 직접 대출 (기존 방식 유지)
    @Transactional
    public Loan borrow(Book book, Member member) {
        if (isBookBorrowed(book.getId())) {
            throw new RuntimeException("이미 대출 중인 도서입니다: " + book.getTitle());
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setReturnDueDate(LocalDate.now().plusDays(14));
        loan.setStatus("APPROVED");
        loan.setCreatedAt(LocalDateTime.now());
        return loanRepository.save(loan);
    }

    // 관리자 직접 반납 (기존 방식 유지)
    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = findById(loanId);
        loan.setReturnedDate(LocalDate.now());
        loan.setStatus("RETURNED");
        return loanRepository.save(loan);
    }

    // 특정 회원의 현재 대출 건수
    public long countActiveLoansByMember(Long memberId) {
        return loanRepository.findByMemberIdAndReturnedDateIsNull(memberId).size();
    }

    // 특정 회원의 대출 기록 (전체)
    public List<Loan> findByMemberId(Long memberId) {
        return loanRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 특정 도서의 전체 대출 이력 (최신순)
    public List<Loan> findByBookId(Long bookId) {
        return loanRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    // 특정 회원의 대출 기록 전체 삭제 (회원 삭제 시 사용)
    @Transactional
    public void deleteByMemberId(Long memberId) {
        loanRepository.deleteByMemberId(memberId);
    }

    // 특정 도서의 대출 기록 전체 삭제 (도서 삭제 시 사용)
    @Transactional
    public void deleteByBookId(Long bookId) {
        loanRepository.deleteByBookId(bookId);
    }
}
