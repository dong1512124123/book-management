package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.LoanService;
import com.example.bookmanagement.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// 대출/반납 관리 컨트롤러
@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;

    public LoanController(LoanService loanService, BookService bookService, MemberService memberService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    // 대출/반납 내역 (상태별 필터링 지원)
    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isEmpty()) {
            model.addAttribute("loans", loanService.findByStatus(status));
            model.addAttribute("currentStatus", status);
        } else {
            model.addAttribute("loans", loanService.findAll());
        }
        // 상태별 건수 (탭 뱃지용)
        model.addAttribute("pendingCount", loanService.countPendingRequests());
        model.addAttribute("returnRequestCount", loanService.countReturnRequests());
        return "loan/list";
    }

    // 대출 처리 폼 (도서 선택 + 회원 선택)
    @GetMapping("/new")
    public String newForm(Model model) {
        // 대출 가능한 도서 목록 (현재 대출 중이 아닌 것만)
        Set<Long> borrowedIds = loanService.getBorrowedBookIds();
        List<Book> allBooks = bookService.findAll();
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (!borrowedIds.contains(book.getId())) {
                availableBooks.add(book);
            }
        }

        model.addAttribute("books", availableBooks);
        model.addAttribute("members", memberService.findAll());
        return "loan/form";
    }

    // 대출 처리 실행
    @PostMapping
    public String create(@RequestParam Long bookId,
                         @RequestParam Long memberId,
                         RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(bookId);
        Member member = memberService.findById(memberId);
        loanService.borrow(book, member);
        redirectAttributes.addFlashAttribute("message",
                "'" + book.getTitle() + "' 도서가 " + member.getName() + " 회원에게 대출되었습니다.");
        return "redirect:/loans";
    }

    // 반납 처리 (관리자 직접)
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.returnBook(id);
        redirectAttributes.addFlashAttribute("message", "반납 처리되었습니다.");
        return "redirect:/loans";
    }

    // 대출 승인 (관리자)
    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.approveLoan(id);
        redirectAttributes.addFlashAttribute("message", "대출이 승인되었습니다.");
        return "redirect:/loans";
    }

    // 대출 거절 (관리자)
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.rejectLoan(id);
        redirectAttributes.addFlashAttribute("message", "대출 신청이 거절되었습니다.");
        return "redirect:/loans";
    }

    // 반납 확인 (관리자 → 사용자 반납 요청 승인)
    @PostMapping("/{id}/confirm-return")
    public String confirmReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.confirmReturn(id);
        redirectAttributes.addFlashAttribute("message", "반납이 확인되었습니다.");
        return "redirect:/loans";
    }
}
