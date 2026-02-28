package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.LoginHistory;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.ExcelService;
import com.example.bookmanagement.service.LoanService;
import com.example.bookmanagement.service.LoginHistoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Set;

// 엑셀 다운로드 컨트롤러
@Controller
@RequestMapping("/excel")
public class ExcelController {

    private final ExcelService excelService;
    private final BookService bookService;
    private final LoanService loanService;
    private final LoginHistoryService loginHistoryService;

    public ExcelController(ExcelService excelService, BookService bookService,
                           LoanService loanService, LoginHistoryService loginHistoryService) {
        this.excelService = excelService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.loginHistoryService = loginHistoryService;
    }

    // 도서 목록 엑셀 다운로드
    @GetMapping("/books")
    public ResponseEntity<byte[]> bookListExcel(
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        try {
            List<Book> books;
            if (keyword.isEmpty()) {
                books = bookService.findAll();
            } else {
                books = bookService.search(type, keyword);
            }
            Set<Long> borrowedIds = loanService.getBorrowedBookIds();
            byte[] excelBytes = excelService.generateBookListExcel(books, borrowedIds);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.setContentDispositionFormData("attachment", "book-list.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 특정 도서의 대출 이력 엑셀 다운로드
    @GetMapping("/books/{id}/loan-history")
    public ResponseEntity<byte[]> loanHistoryExcel(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            List<Loan> loans = loanService.findByBookId(id);
            byte[] excelBytes = excelService.generateLoanHistoryExcel(book, loans);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.setContentDispositionFormData("attachment", "loan-history.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 대출/반납 목록 엑셀 다운로드
    @GetMapping("/loans")
    public ResponseEntity<byte[]> loanListExcel(
            @RequestParam(required = false) String status) {
        try {
            List<Loan> loans;
            if (status != null && !status.isEmpty()) {
                loans = loanService.findByStatus(status);
            } else {
                loans = loanService.findAll();
            }
            byte[] excelBytes = excelService.generateLoanListExcel(loans);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.setContentDispositionFormData("attachment", "loan-list.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 로그인 이력 엑셀 다운로드
    @GetMapping("/login-history")
    public ResponseEntity<byte[]> loginHistoryExcel() {
        try {
            List<LoginHistory> histories = loginHistoryService.findAll();
            byte[] excelBytes = excelService.generateLoginHistoryExcel(histories);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.setContentDispositionFormData("attachment", "login-history.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
