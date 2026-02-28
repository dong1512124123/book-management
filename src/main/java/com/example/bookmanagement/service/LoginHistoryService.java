package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.LoginHistory;
import com.example.bookmanagement.repository.LoginHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

// 로그인 이력 관련 비즈니스 로직
@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryService(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    // 로그인 이력 저장
    public void saveLoginHistory(String userType, Long userId, String username, String ipAddress) {
        LoginHistory history = new LoginHistory();
        history.setUserType(userType);
        history.setUserId(userId);
        history.setUsername(username);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        loginHistoryRepository.save(history);
    }

    // 전체 로그인 이력 조회 (최신순)
    public List<LoginHistory> findAll() {
        return loginHistoryRepository.findAllByOrderByLoginTimeDesc();
    }

    // 특정 사용자의 로그인 이력
    public List<LoginHistory> findByUsername(String username) {
        return loginHistoryRepository.findByUsernameOrderByLoginTimeDesc(username);
    }
}
