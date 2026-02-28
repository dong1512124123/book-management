package com.example.bookmanagement.config;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.AdminRepository;
import com.example.bookmanagement.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Optional;

// Spring Security 설정
// 누가 어떤 페이지에 접근할 수 있는지 정의
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    public SecurityConfig(AdminRepository adminRepository, MemberRepository memberRepository) {
        this.adminRepository = adminRepository;
        this.memberRepository = memberRepository;
    }

    // 비밀번호 암호화 도구 (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 로그인 시 사용자 정보를 어디서 찾을지 정의
    // admin 테이블 → ROLE_ADMIN, member 테이블 → ROLE_USER
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 1) 관리자 테이블에서 찾기
            Optional<Admin> admin = adminRepository.findByUsername(username);
            if (admin.isPresent()) {
                Admin a = admin.get();
                return User.builder()
                        .username(a.getUsername())
                        .password(a.getPassword())
                        .roles("ADMIN")
                        .build();
            }

            // 2) 회원 테이블에서 찾기
            Optional<Member> member = memberRepository.findByUsername(username);
            if (member.isPresent()) {
                Member m = member.get();
                return User.builder()
                        .username(m.getUsername())
                        .password(m.getPassword())
                        .roles("USER")
                        .build();
            }

            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        };
    }

    // URL별 접근 권한 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                            LoginSuccessHandler loginSuccessHandler) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입, 비밀번호 찾기 → 누구나 접근 가능
                .requestMatchers("/auth/**").permitAll()
                // CSS, JS, 이미지 등 정적 파일 → 누구나 접근 가능
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // 관리자 전용 페이지
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 도서/회원/대출 관리 → 관리자만
                .requestMatchers("/books/**", "/members/**", "/loans/**").hasRole("ADMIN")
                // 대시보드 → 관리자만
                .requestMatchers("/dashboard").hasRole("ADMIN")
                // 사용자 전용 페이지
                .requestMatchers("/user/**").hasRole("USER")
                // 알림 → 로그인한 사람 모두
                .requestMatchers("/notifications/**").authenticated()
                // PDF → 로그인한 사람 모두
                .requestMatchers("/pdf/**").authenticated()
                // 나머지 → 로그인 필요
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // 커스텀 로그인 페이지 사용
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                // 로그인 성공 시 이력 기록 + 역할별 리다이렉트
                .successHandler(loginSuccessHandler)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
