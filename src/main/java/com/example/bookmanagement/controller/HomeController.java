package com.example.bookmanagement.controller;

import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.LoanService;
import com.example.bookmanagement.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 대시보드(메인 화면) 컨트롤러
@Controller
public class HomeController {

    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService;

    public HomeController(BookService bookService, MemberService memberService, LoanService loanService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.loanService = loanService;
    }

    // 루트 URL → 역할에 따라 분기
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if ("ROLE_USER".equals(role)) {
                return "redirect:/user/dashboard";
            }
        }
        return "redirect:/dashboard";
    }

    // 대시보드 화면
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalBooks = bookService.count();
        long totalMembers = memberService.count();
        long activeLoans = loanService.countActiveLoans();
        long availableBooks = totalBooks - activeLoans;

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("recentLoans", loanService.findActiveLoans());
        model.addAttribute("pendingCount", loanService.countPendingRequests());
        model.addAttribute("returnRequestCount", loanService.countReturnRequests());

        return "dashboard";
    }
}
