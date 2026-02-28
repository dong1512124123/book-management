package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.service.AdminService;
import com.example.bookmanagement.service.MemberService;
import com.example.bookmanagement.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 알림 컨트롤러
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AdminService adminService;
    private final MemberService memberService;

    public NotificationController(NotificationService notificationService,
                                   AdminService adminService,
                                   MemberService memberService) {
        this.notificationService = notificationService;
        this.adminService = adminService;
        this.memberService = memberService;
    }

    // 알림 목록 페이지
    @GetMapping
    public String list(Authentication authentication, Model model) {
        String recipientType;
        Long recipientId;

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) {
            Admin admin = adminService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("관리자 정보를 찾을 수 없습니다."));
            recipientType = "ADMIN";
            recipientId = admin.getId();
        } else {
            Member member = memberService.findByUsername(authentication.getName());
            recipientType = "USER";
            recipientId = member.getId();
        }

        model.addAttribute("notifications", notificationService.getNotifications(recipientType, recipientId));
        return "notification/list";
    }

    // 알림 읽음 처리
    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    // 모든 알림 읽음 처리
    @PostMapping("/read-all")
    public String markAllAsRead(Authentication authentication, RedirectAttributes redirectAttributes) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) {
            Admin admin = adminService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("관리자 정보를 찾을 수 없습니다."));
            notificationService.markAllAsRead("ADMIN", admin.getId());
        } else {
            Member member = memberService.findByUsername(authentication.getName());
            notificationService.markAllAsRead("USER", member.getId());
        }
        redirectAttributes.addFlashAttribute("message", "모든 알림을 읽음 처리했습니다.");
        return "redirect:/notifications";
    }
}
