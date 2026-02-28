package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.service.AdminService;
import com.example.bookmanagement.service.NoticeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 공지사항 관리 컨트롤러 (관리자 전용)
@Controller
public class NoticeController {

    private final NoticeService noticeService;
    private final AdminService adminService;

    public NoticeController(NoticeService noticeService, AdminService adminService) {
        this.noticeService = noticeService;
        this.adminService = adminService;
    }

    // 공지 목록 (관리자)
    @GetMapping("/admin/notices")
    public String list(Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "admin/notice-list";
    }

    // 공지 작성 폼
    @GetMapping("/admin/notices/new")
    public String newForm() {
        return "admin/notice-form";
    }

    // 공지 작성 처리
    @PostMapping("/admin/notices")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        // 작성자 이름 가져오기
        Admin admin = adminService.findByUsername(authentication.getName()).orElseThrow();
        String authorName = (admin.getName() != null && !admin.getName().isEmpty())
                ? admin.getName() : admin.getUsername();
        noticeService.create(title, content, authorName);
        redirectAttributes.addFlashAttribute("message", "공지사항이 등록되었습니다.");
        return "redirect:/admin/notices";
    }

    // 공지 상세 (관리자)
    @GetMapping("/admin/notices/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.findById(id));
        model.addAttribute("from", "admin");
        return "notice/detail";
    }

    // 공지 수정 폼
    @GetMapping("/admin/notices/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.findById(id));
        return "admin/notice-form";
    }

    // 공지 수정 처리
    @PostMapping("/admin/notices/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         RedirectAttributes redirectAttributes) {
        noticeService.update(id, title, content);
        redirectAttributes.addFlashAttribute("message", "공지사항이 수정되었습니다.");
        return "redirect:/admin/notices";
    }

    // 공지 삭제
    @PostMapping("/admin/notices/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        noticeService.delete(id);
        redirectAttributes.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
        return "redirect:/admin/notices";
    }

    // 공지 목록 (사용자)
    @GetMapping("/user/notices")
    public String userList(Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "user/notices";
    }

    // 공지 상세 (사용자)
    @GetMapping("/user/notices/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.findById(id));
        model.addAttribute("from", "user");
        return "notice/detail";
    }
}
