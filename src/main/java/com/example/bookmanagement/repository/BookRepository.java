package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// @Repository: 이 인터페이스가 DB 접근 담당임을 선언
// JpaRepository<Book, Long>을 상속하면 기본 CRUD 메서드가 자동 제공됨:
//   - findAll(): 전체 조회
//   - findById(id): 단건 조회
//   - save(book): 저장/수정
//   - deleteById(id): 삭제
// 즉, SQL을 직접 작성하지 않아도 됨!
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // --- 검색용 커스텀 메서드 ---
    // Spring Data JPA는 메서드 이름만으로 SQL을 자동 생성해줌
    // findByTitleContaining("자바") → SELECT * FROM book WHERE title LIKE '%자바%'

    // 제목으로 검색 (부분 일치, 대소문자 무시)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // 저자로 검색
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // 출판사로 검색
    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    // ISBN으로 검색
    List<Book> findByIsbnContaining(String isbn);
}
