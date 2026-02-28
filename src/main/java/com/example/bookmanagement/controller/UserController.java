package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.LoanService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Optional;
import java.util.Set;

// 사용자(일반 회원) 전용 컨트롤러
@Controller
@RequestMapping("/user")
public class UserController {

    private final BookService bookService;
    private final LoanService loanService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(BookService bookService, LoanService loanService,
                          MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 현재 로그인한 회원 가져오기
    private Member getCurrentMember(Authentication authentication) {
        return memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    // 사용자 대시보드
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Member member = getCurrentMember(authentication);
        model.addAttribute("member", member);
        model.addAttribute("myLoans", loanService.findByMemberId(member.getId()));
        model.addAttribute("totalBooks", bookService.count());
        return "user/dashboard";
    }

    // 도서 목록 (대출 신청 가능)
    @GetMapping("/books")
    public String books(@RequestParam(required = false, defaultValue = "all") String type,
                        @RequestParam(required = false, defaultValue = "") String keyword,
                        Model model) {
        List<Book> books;
        if (keyword.isEmpty()) {
            books = bookService.findAll();
        } else {
            books = bookService.search(type, keyword);
        }

        Set<Long> borrowedIds = loanService.getBorrowedBookIds();
        model.addAttribute("books", books);
        model.addAttribute("borrowedBookIds", borrowedIds);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "user/books";
    }

    // 도서 상세 (사용자용)
    // 본인 대출이면 정보 전체 표시, 타인이면 마스킹 처리
    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable Long id,
                             @RequestParam(required = false, defaultValue = "user-books") String from,
                             Authentication authentication,
                             Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("isBorrowed", loanService.isBookBorrowed(id));
        model.addAttribute("from", from);

        // 대출 중이면 대여자 정보 추가
        Loan activeLoan = loanService.findActiveLoanByBookId(id);
        if (activeLoan != null) {
            model.addAttribute("activeLoan", activeLoan);
            Member currentMember = getCurrentMember(authentication);
            boolean isOwner = activeLoan.getMember().getId().equals(currentMember.getId());

            if (isOwner) {
                // 본인 대출: 전체 정보 표시
                model.addAttribute("borrowerName", activeLoan.getMember().getName());
                model.addAttribute("borrowerPhone", formatPhone(activeLoan.getMember().getPhone()));
            } else {
                // 타인 대출: 마스킹 처리 (김** / ***-****-****)
                model.addAttribute("borrowerName", maskName(activeLoan.getMember().getName()));
                model.addAttribute("borrowerPhone", "***-****-****");
            }
        }

        // 대출 이력 추가
        model.addAttribute("loanHistory", loanService.findByBookId(id));

        return "book/detail";
    }

    // 이름 마스킹 (김동현 → 김**)
    private String maskName(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.charAt(0) + "**";
    }

    // 전화번호 포맷팅 (01012345678 → 010-1234-5678)
    private String formatPhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        return phone;
    }

    // 대출 신청
    @PostMapping("/books/{bookId}/request")
    public String requestLoan(@PathVariable Long bookId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Member member = getCurrentMember(authentication);
        Book book = bookService.findById(bookId);
        try {
            loanService.requestLoan(book, member);
            redirectAttributes.addFlashAttribute("message",
                    "'" + book.getTitle() + "' 대출 신청이 완료되었습니다. 관리자 승인을 기다려주세요.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/books";
    }

    // 내 대출 현황
    @GetMapping("/my-loans")
    public String myLoans(Authentication authentication, Model model) {
        Member member = getCurrentMember(authentication);
        model.addAttribute("loans", loanService.findByMemberId(member.getId()));
        return "user/my-loans";
    }

    // 반납 신청
    @PostMapping("/loans/{id}/return")
    public String requestReturn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loanService.requestReturn(id);
        redirectAttributes.addFlashAttribute("message", "반납 신청이 완료되었습니다. 관리자 확인을 기다려주세요.");
        return "redirect:/user/my-loans";
    }

    // 사용자 정보 수정 페이지
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        Member member = getCurrentMember(authentication);
        model.addAttribute("member", member);
        return "user/profile";
    }

    // 사용자 정보 수정 처리
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String phone,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Member member = getCurrentMember(authentication);
        member.setName(name);
        member.setPhone(phone.replaceAll("-", ""));
        memberRepository.save(member);
        redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");
        return "redirect:/user/profile";
    }

    // 사용자 비밀번호 변경
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Member member = getCurrentMember(authentication);
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/user/profile";
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/user/profile";
    }
}
