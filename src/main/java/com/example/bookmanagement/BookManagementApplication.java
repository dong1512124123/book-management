package com.example.bookmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication: 이 한 줄이 아래 3가지를 동시에 해줌
// 1) @Configuration - 이 클래스가 설정 파일 역할
// 2) @EnableAutoConfiguration - 필요한 설정을 자동으로 잡아줌
// 3) @ComponentScan - 같은 패키지 아래의 모든 클래스를 자동 등록
@SpringBootApplication
public class BookManagementApplication {

    // main 메서드: Java 프로그램의 시작점
    // SpringApplication.run()이 내장 톰캣 서버를 띄우고 앱을 실행함
    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class, args);
    }
}
