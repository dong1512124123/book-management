package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Notice;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// 공지사항 비즈니스 로직 담당
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    public NoticeService(NoticeRepository noticeRepository,
                         NotificationService notificationService,
                         MemberRepository memberRepository) {
        this.noticeRepository = noticeRepository;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
    }

    // 전체 공지 목록 (최신순)
    public List<Notice> findAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    // 공지 상세 조회
    public Notice findById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다. ID: " + id));
    }

    // 공지 작성 + 전체 사용자에게 알림 발송
    @Transactional
    public Notice create(String title, String content, String author) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAuthor(author);
        notice.setCreatedAt(LocalDateTime.now());
        Notice saved = noticeRepository.save(notice);

        // 모든 사용자(회원)에게 알림 전송
        memberRepository.findAll().forEach(member -> {
            notificationService.notifyUser(
                    member.getId(),
                    "NOTICE",
                    "새 공지사항",
                    "'" + title + "' 공지사항이 등록되었습니다.");
        });

        return saved;
    }

    // 공지 수정
    @Transactional
    public Notice update(Long id, String title, String content) {
        Notice notice = findById(id);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setUpdatedAt(LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    // 공지 삭제
    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    // 공지 수
    public long count() {
        return noticeRepository.count();
    }
}
