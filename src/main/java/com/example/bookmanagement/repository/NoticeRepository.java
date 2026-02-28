package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 공지사항 DB 접근 담당
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 최신순 정렬
    List<Notice> findAllByOrderByCreatedAtDesc();
}
