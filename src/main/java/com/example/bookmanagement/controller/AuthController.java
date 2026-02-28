package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Optional;

// 인증 관련 컨트롤러 (로그인, 회원가입, 비밀번호 찾기)
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AdminService adminService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AdminService adminService, MemberRepository memberRepository,
                          PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String phone,
                           @RequestParam String username,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        // 아이디 중복 확인
        if (memberRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/auth/register";
        }
        if (adminService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/auth/register";
        }

        Member member = new Member();
        member.setName(name);
        member.setPhone(phone);
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setRole("USER");
        member.setCreatedAt(LocalDateTime.now());
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/auth/login";
    }

    // 비밀번호 찾기 페이지
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    // 비밀번호 찾기 처리 (이름 + 전화번호로 본인 확인 → 비밀번호 초기화)
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String name,
                                 @RequestParam String phone,
                                 @RequestParam String username,
                                 RedirectAttributes redirectAttributes) {
        // 관리자에서 찾기
        Optional<Admin> admin = adminService.findByUsername(username);
        if (admin.isPresent()) {
            Admin a = admin.get();
            if (a.getName() != null && a.getName().equals(name) &&
                a.getPhone() != null && a.getPhone().equals(phone.replaceAll("-", ""))) {
                adminService.resetPassword(a.getId());
                redirectAttributes.addFlashAttribute("message",
                        "비밀번호가 1234로 초기화되었습니다. 로그인 후 변경해주세요.");
                return "redirect:/auth/login";
            }
        }

        // 사용자에서 찾기
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            Member m = member.get();
            if (m.getName().equals(name) &&
                m.getPhone().equals(phone.replaceAll("-", ""))) {
                m.setPassword(passwordEncoder.encode("1234"));
                memberRepository.save(m);
                redirectAttributes.addFlashAttribute("message",
                        "비밀번호가 1234로 초기화되었습니다. 로그인 후 변경해주세요.");
                return "redirect:/auth/login";
            }
        }

        redirectAttributes.addFlashAttribute("error", "입력한 정보와 일치하는 계정을 찾을 수 없습니다.");
        return "redirect:/auth/forgot-password";
    }
}
