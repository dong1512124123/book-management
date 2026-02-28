package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 로그인 이력 DB 접근 담당
@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    // 전체 로그인 이력 (최신순)
    List<LoginHistory> findAllByOrderByLoginTimeDesc();

    // 특정 사용자의 로그인 이력
    List<LoginHistory> findByUsernameOrderByLoginTimeDesc(String username);
}
