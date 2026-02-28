package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// 도서 관련 비즈니스 로직 담당
// 대출/반납은 LoanService로 분리됨
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Path uploadDir = Paths.get("uploads/covers");

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }
    }

    // 표지 이미지 저장
    public String saveCoverImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + ext;
        Files.copy(file.getInputStream(), uploadDir.resolve(fileName));
        return fileName;
    }

    // 표지 이미지 삭제
    public void deleteCoverImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;
        try {
            Files.deleteIfExists(uploadDir.resolve(fileName));
        } catch (IOException e) {
            // 파일 삭제 실패 시 무시
        }
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("도서를 찾을 수 없습니다. ID: " + id));
    }

    public long count() {
        return bookRepository.count();
    }

    public Book save(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book updatedBook) {
        Book book = findById(id);
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPublisher(updatedBook.getPublisher());
        book.setIsbn(updatedBook.getIsbn());
        book.setDescription(updatedBook.getDescription());
        book.setCoverImage(updatedBook.getCoverImage());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    // 검색 기능
    public List<Book> search(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        switch (type) {
            case "title":
                return bookRepository.findByTitleContainingIgnoreCase(keyword);
            case "author":
                return bookRepository.findByAuthorContainingIgnoreCase(keyword);
            case "publisher":
                return bookRepository.findByPublisherContainingIgnoreCase(keyword);
            case "isbn":
                return bookRepository.findByIsbnContaining(keyword);
            default:
                List<Book> result = bookRepository.findByTitleContainingIgnoreCase(keyword);
                for (Book b : bookRepository.findByAuthorContainingIgnoreCase(keyword)) {
                    if (!result.contains(b)) result.add(b);
                }
                for (Book b : bookRepository.findByPublisherContainingIgnoreCase(keyword)) {
                    if (!result.contains(b)) result.add(b);
                }
                for (Book b : bookRepository.findByIsbnContaining(keyword)) {
                    if (!result.contains(b)) result.add(b);
                }
                return result;
        }
    }
}
