package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// 회원 DB 접근 담당
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이름으로 검색
    List<Member> findByNameContainingIgnoreCase(String name);

    // 전화번호로 검색
    List<Member> findByPhoneContaining(String phone);

    // 아이디로 회원 찾기 (로그인용)
    Optional<Member> findByUsername(String username);

    // 이름 + 전화번호로 회원 찾기 (비밀번호 찾기용)
    Optional<Member> findByNameAndPhone(String name, String phone);

    // 아이디 중복 확인
    boolean existsByUsername(String username);
}
