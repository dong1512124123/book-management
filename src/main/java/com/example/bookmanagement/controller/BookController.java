package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

// 도서 관리 컨트롤러 (CRUD만 담당, 대출/반납은 LoanController)
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    // 도서 목록 + 검색
    @GetMapping
    public String list(
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {

        if (keyword.isEmpty()) {
            model.addAttribute("books", bookService.findAll());
        } else {
            model.addAttribute("books", bookService.search(type, keyword));
        }
        model.addAttribute("borrowedBookIds", loanService.getBorrowedBookIds());
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        return "book/list";
    }

    // 도서 등록 폼
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/form";
    }

    // 도서 등록 처리
    @PostMapping
    public String create(Book book,
                         @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                         RedirectAttributes redirectAttributes) throws IOException {
        if (coverFile != null && !coverFile.isEmpty()) {
            String fileName = bookService.saveCoverImage(coverFile);
            book.setCoverImage(fileName);
        }
        bookService.save(book);
        redirectAttributes.addFlashAttribute("message", "도서가 등록되었습니다.");
        return "redirect:/books";
    }

    // 도서 상세 (from 파라미터로 돌아갈 페이지 결정)
    // 관리자 전용: 대여자 정보를 전부 표시
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false, defaultValue = "books") String from,
                         Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("isBorrowed", loanService.isBookBorrowed(id));
        model.addAttribute("from", from);

        // 대출 중이면 대여자 정보 추가 (관리자는 전체 공개)
        Loan activeLoan = loanService.findActiveLoanByBookId(id);
        if (activeLoan != null) {
            model.addAttribute("activeLoan", activeLoan);
            model.addAttribute("borrowerName", activeLoan.getMember().getName());
            model.addAttribute("borrowerPhone", formatPhone(activeLoan.getMember().getPhone()));
        }

        // 대출 이력 추가
        model.addAttribute("loanHistory", loanService.findByBookId(id));

        return "book/detail";
    }

    // 전화번호 포맷팅 (01012345678 → 010-1234-5678)
    private String formatPhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        return phone;
    }

    // 도서 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "book/form";
    }

    // 도서 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, Book book,
                         @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                         @RequestParam(value = "deleteCover", required = false) boolean deleteCover,
                         RedirectAttributes redirectAttributes) throws IOException {
        Book existing = bookService.findById(id);
        if (deleteCover && existing.getCoverImage() != null) {
            bookService.deleteCoverImage(existing.getCoverImage());
            book.setCoverImage(null);
        } else if (coverFile != null && !coverFile.isEmpty()) {
            // 기존 이미지 삭제 후 새 이미지 저장
            if (existing.getCoverImage() != null) {
                bookService.deleteCoverImage(existing.getCoverImage());
            }
            String fileName = bookService.saveCoverImage(coverFile);
            book.setCoverImage(fileName);
        } else {
            // 이미지 변경 없음 → 기존 유지
            book.setCoverImage(existing.getCoverImage());
        }
        bookService.update(id, book);
        redirectAttributes.addFlashAttribute("message", "도서 정보가 수정되었습니다.");
        return "redirect:/books";
    }

    // 도서 삭제 (대출 중인 도서는 삭제 불가)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (loanService.isBookBorrowed(id)) {
            redirectAttributes.addFlashAttribute("error", "대출 중인 도서는 삭제할 수 없습니다. 반납 후 다시 시도해주세요.");
            return "redirect:/books";
        }
        // 표지 이미지 삭제
        Book book = bookService.findById(id);
        if (book.getCoverImage() != null) {
            bookService.deleteCoverImage(book.getCoverImage());
        }
        // 과거 대출 이력 먼저 삭제 (외래키 제약조건)
        loanService.deleteByBookId(id);
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("message", "도서가 삭제되었습니다.");
        return "redirect:/books";
    }
}
