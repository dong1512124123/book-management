package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// 관리자 관련 비즈니스 로직
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다. ID: " + id));
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }

    // 관리자 등록 (비밀번호 암호화)
    public Admin save(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());
        return adminRepository.save(admin);
    }

    // 최초 로그인 시 관리자 정보(이름, 전화번호) 입력
    @Transactional
    public Admin setupProfile(Long id, String name, String phone) {
        Admin admin = findById(id);
        admin.setName(name);
        admin.setPhone(phone);
        admin.setFirstLogin(false);
        return adminRepository.save(admin);
    }

    // 관리자 정보 수정
    @Transactional
    public Admin updateProfile(Long id, String name, String phone) {
        Admin admin = findById(id);
        admin.setName(name);
        admin.setPhone(phone);
        return adminRepository.save(admin);
    }

    // 비밀번호 변경
    @Transactional
    public Admin changePassword(Long id, String newPassword) {
        Admin admin = findById(id);
        admin.setPassword(passwordEncoder.encode(newPassword));
        return adminRepository.save(admin);
    }

    // 비밀번호 찾기 (이름 + 전화번호로 본인 확인)
    public Optional<Admin> findByNameAndPhone(String name, String phone) {
        return adminRepository.findByNameAndPhone(name, phone);
    }

    // 비밀번호 초기화 (1234로 리셋)
    @Transactional
    public void resetPassword(Long id) {
        Admin admin = findById(id);
        admin.setPassword(passwordEncoder.encode("1234"));
        adminRepository.save(admin);
    }
}
