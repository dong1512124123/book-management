package com.example.bookmanagement.config;

import com.example.bookmanagement.entity.Admin;
import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.repository.AdminRepository;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.LoanRepository;
import com.example.bookmanagement.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 앱 시작 시 기본 관리자 계정 + 테스트 데이터를 자동 생성
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
                           MemberRepository memberRepository,
                           BookRepository bookRepository,
                           LoanRepository loanRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 관리자가 한 명도 없으면 기본 계정 생성
        if (adminRepository.count() == 0) {
            Admin admin = new Admin("admin", passwordEncoder.encode("1234"));
            admin.setFirstLogin(true);
            adminRepository.save(admin);
            System.out.println("=== 기본 관리자 계정 생성: admin / 1234 ===");
        }

        // 테스트 사용자 10명 생성 (비밀번호 = 이름 영타, 이미 있는 username은 건너뜀)
        // {이름, 전화번호, 아이디(=이름), 비밀번호(이름 영타)}
        String[][] users = {
            {"김민수", "010-1234-5678", "김민수", "rlaalstn"},
            {"이서연", "010-2345-6789", "이서연", "dltjdus"},
            {"박지훈", "010-3456-7890", "박지훈", "qkrwlgns"},
            {"최유진", "010-4567-8901", "최유진", "chldbwls"},
            {"정하늘", "010-5678-9012", "정하늘", "wjdgksmf"},
            {"강도현", "010-6789-0123", "강도현", "rkdehgus"},
            {"윤서아", "010-7890-1234", "윤서아", "dbstjdk"},
            {"임재원", "010-8901-2345", "임재원", "dlawodnjs"},
            {"한소희", "010-9012-3456", "한소희", "gksthgml"},
            {"오준영", "010-0123-4567", "오준영", "dhwnsdud"}
        };
        int userCount = 0;
        for (String[] u : users) {
            if (memberRepository.findByUsername(u[2]).isEmpty()) {
                Member m = new Member(u[0], u[1], u[2], passwordEncoder.encode(u[3]));
                memberRepository.save(m);
                userCount++;
            }
        }
        if (userCount > 0) {
            System.out.println("=== 테스트 사용자 " + userCount + "명 생성 (비밀번호 = 이름 영타) ===");
        }

        // 테스트 도서 20권 생성 (이미 같은 제목이 있으면 건너뜀)
        String[][] books = {
                {"자바의 정석", "남궁성", "도우출판", "9788994492032", "자바 프로그래밍의 기초부터 심화까지 체계적으로 다룬 국내 대표 자바 입문서"},
                {"스프링 부트와 AWS로 혼자 구현하는 웹 서비스", "이동욱", "프리렉", "9788965402602", "스프링 부트와 AWS를 활용한 웹 서비스 개발 실전 가이드"},
                {"클린 코드", "로버트 C. 마틴", "인사이트", "9788966260959", "읽기 좋고 유지보수하기 쉬운 코드를 작성하는 방법을 다룬 프로그래밍 필독서"},
                {"이펙티브 자바", "조슈아 블로크", "인사이트", "9788966262281", "자바 프로그래밍 언어의 베스트 프랙티스 78가지를 소개"},
                {"객체지향의 사실과 오해", "조영호", "위키북스", "9788998139766", "객체지향 프로그래밍의 본질을 쉽게 풀어낸 입문서"},
                {"모던 자바 인 액션", "라울-게이브리얼 우르마", "한빛미디어", "9791162242025", "자바 8~11의 새로운 기능과 함수형 프로그래밍을 소개"},
                {"Do it! 자바 프로그래밍 입문", "박은종", "이지스퍼블리싱", "9791163030195", "처음 자바를 배우는 사람을 위한 친절한 자바 입문서"},
                {"헤드 퍼스트 디자인 패턴", "에릭 프리먼", "한빛미디어", "9791162245262", "디자인 패턴을 재미있게 학습할 수 있는 입문서"},
                {"토비의 스프링 3.1", "이일민", "에이콘", "9788960773431", "스프링 프레임워크의 핵심 원리를 깊이 있게 다룬 국내 대표 스프링 서적"},
                {"리팩터링", "마틴 파울러", "한빛미디어", "9791162242742", "코드의 구조를 개선하는 체계적인 리팩터링 기법 소개"},
                {"데이터베이스 개론", "김연희", "한빛아카데미", "9791156644316", "데이터베이스의 기본 개념부터 SQL까지 체계적으로 학습"},
                {"혼자 공부하는 컴퓨터 구조+운영체제", "강민철", "한빛미디어", "9791162245071", "컴퓨터 구조와 운영체제를 혼자서도 학습할 수 있는 입문서"},
                {"알고리즘 문제 해결 전략", "구종만", "인사이트", "9788966260546", "프로그래밍 대회에서 사용되는 알고리즘과 자료구조를 다룸"},
                {"점프 투 파이썬", "박응용", "이지스퍼블리싱", "9791163034735", "파이썬 프로그래밍을 처음 배우는 사람을 위한 입문서"},
                {"HTTP 완벽 가이드", "데이빗 고울리", "인사이트", "9788966261208", "HTTP 프로토콜의 동작 원리를 상세히 설명한 레퍼런스"},
                {"그림으로 배우는 네트워크 원리", "Gene", "영진닷컴", "9788931465136", "네트워크의 기본 원리를 그림과 함께 쉽게 이해할 수 있는 입문서"},
                {"개발자의 글쓰기", "김철수", "위키북스", "9791158392635", "개발자가 알아야 할 기술 글쓰기의 모든 것"},
                {"함께 자라기", "김창준", "인사이트", "9788966262335", "애자일로 가는 길, 함께 성장하는 소프트웨어 개발 문화"},
                {"프로그래머의 뇌", "펠리너 헤르만스", "제이펍", "9791191600650", "인지과학 관점에서 프로그래밍 학습과 코드 이해를 돕는 책"},
                {"IT 엔지니어를 위한 네트워크 입문", "고재성", "길벗", "9791165219529", "실무에 필요한 네트워크 개념을 정리한 실용 입문서"}
            };
        int bookCount = 0;
        for (String[] b : books) {
            if (bookRepository.findByTitleContainingIgnoreCase(b[0]).isEmpty()) {
                Book book = new Book(b[0], b[1], b[2], b[3]);
                book.setDescription(b[4]);
                bookRepository.save(book);
                bookCount++;
            }
        }
        if (bookCount > 0) {
            System.out.println("=== 테스트 도서 " + bookCount + "권 생성 ===");
        }

        // 대출/반납 샘플 이력 생성 (이미 대출 기록이 있으면 건너뜀)
        if (loanRepository.count() == 0) {
            List<Member> memberList = memberRepository.findAll();
            List<Book> bookList = bookRepository.findAll();

            if (memberList.size() >= 5 && bookList.size() >= 10) {
                int loanCount = 0;

                // 1) 반납 완료 이력 6건 (과거 대출 → 반납)
                // 김민수 - 자바의 정석 (3주 전 대출 → 1주 전 반납)
                loanCount += createLoan(bookList.get(0), memberList.get(0),
                        LocalDate.now().minusDays(21), LocalDate.now().minusDays(7),
                        LocalDate.now().minusDays(8), "RETURNED");

                // 이서연 - 클린 코드 (4주 전 대출 → 2주 전 반납)
                loanCount += createLoan(bookList.get(2), memberList.get(1),
                        LocalDate.now().minusDays(28), LocalDate.now().minusDays(14),
                        LocalDate.now().minusDays(15), "RETURNED");

                // 박지훈 - 이펙티브 자바 (5주 전 대출 → 3주 전 반납)
                loanCount += createLoan(bookList.get(3), memberList.get(2),
                        LocalDate.now().minusDays(35), LocalDate.now().minusDays(21),
                        LocalDate.now().minusDays(20), "RETURNED");

                // 최유진 - 헤드 퍼스트 디자인 패턴 (6주 전 대출 → 5주 전 반납)
                loanCount += createLoan(bookList.get(7), memberList.get(3),
                        LocalDate.now().minusDays(42), LocalDate.now().minusDays(28),
                        LocalDate.now().minusDays(35), "RETURNED");

                // 정하늘 - 데이터베이스 개론 (3주 전 대출 → 5일 전 반납)
                loanCount += createLoan(bookList.get(10), memberList.get(4),
                        LocalDate.now().minusDays(21), LocalDate.now().minusDays(7),
                        LocalDate.now().minusDays(5), "RETURNED");

                // 강도현 - 점프 투 파이썬 (2주 전 대출 → 3일 전 반납)
                loanCount += createLoan(bookList.get(13), memberList.get(5),
                        LocalDate.now().minusDays(14), LocalDate.now(),
                        LocalDate.now().minusDays(3), "RETURNED");

                // 2) 현재 대출 중 3건 (APPROVED)
                // 김민수 - 모던 자바 인 액션 (1주 전 대출, 반납 예정 7일 후)
                loanCount += createLoan(bookList.get(5), memberList.get(0),
                        LocalDate.now().minusDays(7), LocalDate.now().plusDays(7),
                        null, "APPROVED");

                // 윤서아 - 리팩터링 (10일 전 대출, 반납 예정 4일 후)
                loanCount += createLoan(bookList.get(9), memberList.get(6),
                        LocalDate.now().minusDays(10), LocalDate.now().plusDays(4),
                        null, "APPROVED");

                // 임재원 - 알고리즘 문제 해결 전략 (3일 전 대출, 반납 예정 11일 후)
                loanCount += createLoan(bookList.get(12), memberList.get(7),
                        LocalDate.now().minusDays(3), LocalDate.now().plusDays(11),
                        null, "APPROVED");

                // 3) 승인 대기 1건 (REQUESTED)
                // 한소희 - 객체지향의 사실과 오해 (오늘 신청)
                loanCount += createLoan(bookList.get(4), memberList.get(8),
                        LocalDate.now(), LocalDate.now().plusDays(14),
                        null, "REQUESTED");

                // 4) 반납 대기 1건 (RETURN_REQUESTED)
                // 오준영 - HTTP 완벽 가이드 (2주 전 대출, 반납 신청)
                loanCount += createLoan(bookList.get(14), memberList.get(9),
                        LocalDate.now().minusDays(14), LocalDate.now(),
                        null, "RETURN_REQUESTED");

                System.out.println("=== 샘플 대출 이력 " + loanCount + "건 생성 ===");
            }
        }
    }

    // 대출 기록 생성 헬퍼 메서드
    private int createLoan(Book book, Member member,
                           LocalDate borrowDate, LocalDate returnDueDate,
                           LocalDate returnedDate, String status) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(borrowDate);
        loan.setReturnDueDate(returnDueDate);
        loan.setReturnedDate(returnedDate);
        loan.setStatus(status);
        loan.setCreatedAt(LocalDateTime.of(borrowDate.getYear(), borrowDate.getMonth(),
                borrowDate.getDayOfMonth(), 9, 0));
        loanRepository.save(loan);
        return 1;
    }
}
