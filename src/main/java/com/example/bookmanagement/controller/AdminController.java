package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.service.AdminService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

// 관리자 전용 컨트롤러 (관리자 정보 수정, 관리자 등록)
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    // 최초 로그인 시 관리자 정보 입력 페이지
    @GetMapping("/setup")
    public String setupPage(Authentication authentication, Model model) {
        Optional<Admin> admin = adminService.findByUsername(authentication.getName());
        if (admin.isPresent() && !admin.get().isFirstLogin()) {
            return "redirect:/dashboard";
        }
        return "auth/admin-setup";
    }

    // 최초 로그인 정보 저장
    @PostMapping("/setup")
    public String setup(@RequestParam String name,
                        @RequestParam String phone,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {
        Optional<Admin> admin = adminService.findByUsername(authentication.getName());
        if (admin.isPresent()) {
            adminService.setupProfile(admin.get().getId(), name, phone.replaceAll("-", ""));
            redirectAttributes.addFlashAttribute("message", "관리자 정보가 설정되었습니다.");
        }
        return "redirect:/dashboard";
    }

    // 관리자 정보 수정 페이지
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        Optional<Admin> admin = adminService.findByUsername(authentication.getName());
        admin.ifPresent(a -> model.addAttribute("admin", a));
        return "admin/profile";
    }

    // 관리자 정보 수정 처리
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String phone,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Optional<Admin> admin = adminService.findByUsername(authentication.getName());
        if (admin.isPresent()) {
            adminService.updateProfile(admin.get().getId(), name, phone.replaceAll("-", ""));
            redirectAttributes.addFlashAttribute("message", "관리자 정보가 수정되었습니다.");
        }
        return "redirect:/admin/profile";
    }

    // 비밀번호 변경 처리
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Optional<Admin> admin = adminService.findByUsername(authentication.getName());
        if (admin.isPresent()) {
            if (!passwordEncoder.matches(currentPassword, admin.get().getPassword())) {
                redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                return "redirect:/admin/profile";
            }
            adminService.changePassword(admin.get().getId(), newPassword);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        }
        return "redirect:/admin/profile";
    }

}
