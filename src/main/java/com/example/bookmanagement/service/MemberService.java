package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// 회원 관련 비즈니스 로직 담당
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + id));
    }

    public long count() {
        return memberRepository.count();
    }

    // 아이디(username)로 회원 찾기
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));
    }

    public Member save(Member member) {
        member.setCreatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public Member update(Long id, Member updatedMember) {
        Member member = findById(id);
        member.setName(updatedMember.getName());
        member.setPhone(updatedMember.getPhone());
        return memberRepository.save(member);
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    // 검색 기능
    public List<Member> search(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        switch (type) {
            case "name":
                return memberRepository.findByNameContainingIgnoreCase(keyword);
            case "phone":
                return memberRepository.findByPhoneContaining(keyword);
            default:
                List<Member> result = memberRepository.findByNameContainingIgnoreCase(keyword);
                for (Member m : memberRepository.findByPhoneContaining(keyword)) {
                    if (!result.contains(m)) result.add(m);
                }
                return result;
        }
    }
}
