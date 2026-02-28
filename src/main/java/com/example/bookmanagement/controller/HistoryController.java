package com.example.bookmanagement.controller;

import com.example.bookmanagement.service.LoginHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 이력 관리 컨트롤러 (관리자용)
@Controller
@RequestMapping("/admin/history")
public class HistoryController {

    private final LoginHistoryService loginHistoryService;

    public HistoryController(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;
    }

    // 로그인 이력 페이지
    @GetMapping("/login")
    public String loginHistory(Model model) {
        model.addAttribute("histories", loginHistoryService.findAll());
        return "admin/login-history";
    }
}
