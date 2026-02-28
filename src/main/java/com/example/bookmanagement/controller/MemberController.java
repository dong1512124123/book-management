package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.service.LoanService;
import com.example.bookmanagement.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 회원 관리 컨트롤러
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final LoanService loanService;

    public MemberController(MemberService memberService, LoanService loanService) {
        this.memberService = memberService;
        this.loanService = loanService;
    }

    // 회원 목록 + 검색
    @GetMapping
    public String list(
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {

        if (keyword.isEmpty()) {
            model.addAttribute("members", memberService.findAll());
        } else {
            model.addAttribute("members", memberService.search(type, keyword));
        }
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        return "member/list";
    }

    // 회원 등록 폼
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("member", new Member());
        return "member/form";
    }

    // 회원 등록 처리
    @PostMapping
    public String create(Member member, RedirectAttributes redirectAttributes) {
        memberService.save(member);
        redirectAttributes.addFlashAttribute("message", "회원이 등록되었습니다.");
        return "redirect:/members";
    }

    // 회원 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.findById(id));
        return "member/form";
    }

    // 회원 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, Member member, RedirectAttributes redirectAttributes) {
        memberService.update(id, member);
        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/members";
    }

    // 회원 삭제 (대출 중인 회원은 삭제 불가)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (loanService.countActiveLoansByMember(id) > 0) {
            redirectAttributes.addFlashAttribute("error", "대출 중인 회원은 삭제할 수 없습니다. 반납 후 다시 시도해주세요.");
            return "redirect:/members";
        }
        // 과거 대출 이력 먼저 삭제 (외래키 제약조건)
        loanService.deleteByMemberId(id);
        memberService.delete(id);
        redirectAttributes.addFlashAttribute("message", "회원이 삭제되었습니다.");
        return "redirect:/members";
    }
}
