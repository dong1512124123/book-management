package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// 관리자 DB 접근 담당
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 아이디로 관리자 찾기 (로그인용)
    Optional<Admin> findByUsername(String username);

    // 이름 + 전화번호로 관리자 찾기 (비밀번호 찾기용)
    Optional<Admin> findByNameAndPhone(String name, String phone);

    // 아이디 중복 확인
    boolean existsByUsername(String username);
}
