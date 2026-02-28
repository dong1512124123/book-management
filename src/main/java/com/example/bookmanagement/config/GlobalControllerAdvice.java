package com.example.bookmanagement.config;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.service.AdminService;
import com.example.bookmanagement.service.MemberService;
import com.example.bookmanagement.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.Optional;

// 모든 컨트롤러에 공통으로 모델 데이터를 추가
// → 헤더의 알림 뱃지에 읽지 않은 알림 수 표시
@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificationService notificationService;
    private final AdminService adminService;
    private final MemberService memberService;

    public GlobalControllerAdvice(NotificationService notificationService,
                                   AdminService adminService,
                                   MemberService memberService) {
        this.notificationService = notificationService;
        this.adminService = adminService;
        this.memberService = memberService;
    }

    @ModelAttribute("unreadNotifications")
    public Long unreadNotifications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0L;
        }

        try {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                Optional<Admin> admin = adminService.findByUsername(authentication.getName());
                if (admin.isPresent()) {
                    return notificationService.countUnread("ADMIN", admin.get().getId());
                }
            } else if ("ROLE_USER".equals(role)) {
                Member member = memberService.findByUsername(authentication.getName());
                return notificationService.countUnread("USER", member.getId());
            }
        } catch (Exception e) {
            // 로그인 과정 중 등 예외 발생 시 무시
        }
        return 0L;
    }
}
