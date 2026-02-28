package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.LoginHistory;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.LoanService;
import com.example.bookmanagement.service.LoginHistoryService;
import com.example.bookmanagement.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Set;

// PDF 다운로드/미리보기 컨트롤러
@Controller
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final BookService bookService;
    private final LoanService loanService;
    private final LoginHistoryService loginHistoryService;

    public PdfController(PdfService pdfService, BookService bookService, LoanService loanService, LoginHistoryService loginHistoryService) {
        this.pdfService = pdfService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.loginHistoryService = loginHistoryService;
    }

    // 도서 목록 PDF (미리보기/다운로드)
    // 검색 파라미터가 있으면 검색 결과만 PDF로 생성
    @GetMapping("/books")
    public ResponseEntity<byte[]> bookListPdf(
            @RequestParam(defaultValue = "inline") String disposition,
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
            byte[] pdfBytes = pdfService.generateBookListPdf(books, borrowedIds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            if ("attachment".equals(disposition)) {
                // 다운로드
                headers.setContentDispositionFormData("attachment", "book-list.pdf");
            } else {
                // 미리보기 (브라우저에서 열기)
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=book-list.pdf");
            }

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 특정 도서의 대출 이력 PDF 다운로드
    @GetMapping("/books/{id}/loan-history")
    public ResponseEntity<byte[]> loanHistoryPdf(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            List<Loan> loans = loanService.findByBookId(id);
            byte[] pdfBytes = pdfService.generateLoanHistoryPdf(book, loans);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "loan-history.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 대출/반납 목록 PDF 다운로드
    @GetMapping("/loans")
    public ResponseEntity<byte[]> loanListPdf(
            @RequestParam(required = false) String status) {
        try {
            List<Loan> loans;
            if (status != null && !status.isEmpty()) {
                loans = loanService.findByStatus(status);
            } else {
                loans = loanService.findAll();
            }
            byte[] pdfBytes = pdfService.generateLoanListPdf(loans);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "loan-list.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 로그인 이력 PDF 다운로드
    @GetMapping("/login-history")
    public ResponseEntity<byte[]> loginHistoryPdf() {
        try {
            List<LoginHistory> histories = loginHistoryService.findAll();
            byte[] pdfBytes = pdfService.generateLoginHistoryPdf(histories);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "login-history.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
