package com.example.bookmanagement.config;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.AdminRepository;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.service.LoginHistoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

// 로그인 성공 시 실행되는 핸들러
// 1) 로그인 이력 저장
// 2) 역할별 리다이렉트 (관리자: 대시보드/최초설정, 사용자: 사용자 대시보드)
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginHistoryService loginHistoryService;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    public LoginSuccessHandler(LoginHistoryService loginHistoryService,
                                AdminRepository adminRepository,
                                MemberRepository memberRepository) {
        this.loginHistoryService = loginHistoryService;
        this.adminRepository = adminRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String ipAddress = request.getRemoteAddr();

        // 로그인 이력 저장
        if ("ROLE_ADMIN".equals(role)) {
            Optional<Admin> admin = adminRepository.findByUsername(username);
            if (admin.isPresent()) {
                loginHistoryService.saveLoginHistory("ADMIN", admin.get().getId(), username, ipAddress);

                // 최초 로그인이면 정보 입력 페이지로
                if (admin.get().isFirstLogin()) {
                    response.sendRedirect("/admin/setup");
                    return;
                }
            }
            response.sendRedirect("/dashboard");
        } else {
            Optional<Member> member = memberRepository.findByUsername(username);
            if (member.isPresent()) {
                loginHistoryService.saveLoginHistory("USER", member.get().getId(), username, ipAddress);
            }
            response.sendRedirect("/user/dashboard");
        }
    }
}
