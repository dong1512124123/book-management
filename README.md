# LibraryGO - 간이 도서 대출 관리 시스템

학교, 동아리, 소규모 도서관을 위한 간단한 도서 대출 관리 웹 애플리케이션입니다.

## 주요 기능

- **도서 관리**: 도서 등록 / 수정 / 삭제 / 검색
- **회원 관리**: 회원 등록 / 수정 / 삭제 / 검색
- **대출 관리**: 대출 처리 / 반납 처리 / 연체 확인
- **대시보드**: 전체 현황 한눈에 보기

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Spring Boot 3.2.3 (Java 17) |
| Database | MySQL 8.0 |
| View | Thymeleaf |
| Frontend | Bootstrap 5.3.3 (CDN) |
| Build | Maven |

---

## 실행 방법

### 1단계: 사전 준비

아래 프로그램이 설치되어 있어야 합니다.

- **Java 17 이상** - [다운로드](https://adoptium.net/)
- **MySQL 8.0** - [다운로드](https://dev.mysql.com/downloads/installer/)

### 2단계: 프로젝트 다운로드

```bash
git clone https://github.com/dong1512124123/book-management.git
cd book-management
```

### 3단계: MySQL 데이터베이스 생성

MySQL에 접속하여 데이터베이스를 생성합니다.

```sql
CREATE DATABASE book_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4단계: DB 비밀번호 설정

본인의 MySQL root 비밀번호를 환경변수로 설정합니다.

**Windows (CMD):**
```cmd
set DB_PASSWORD=본인의MySQL비밀번호
```

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="본인의MySQL비밀번호"
```

**Mac / Linux:**
```bash
export DB_PASSWORD=본인의MySQL비밀번호
```

> 환경변수를 설정하지 않으면 기본값 `1234`가 사용됩니다.

### 5단계: 실행

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**Mac / Linux:**
```bash
./mvnw spring-boot:run
```

### 6단계: 접속

브라우저에서 아래 주소로 접속합니다.

```
http://localhost:8080
```

---

## 프로젝트 구조

```
book-management/
├── src/main/java/com/example/bookmanagement/
│   ├── BookManagementApplication.java  ← 메인 실행 클래스
│   ├── controller/                     ← 웹 요청 처리
│   │   ├── HomeController.java
│   │   ├── BookController.java
│   │   ├── MemberController.java
│   │   └── LoanController.java
│   ├── service/                        ← 비즈니스 로직
│   │   ├── BookService.java
│   │   ├── MemberService.java
│   │   └── LoanService.java
│   ├── repository/                     ← DB 접근
│   │   ├── BookRepository.java
│   │   ├── MemberRepository.java
│   │   └── LoanRepository.java
│   └── entity/                         ← DB 테이블 매핑
│       ├── Book.java
│       ├── Member.java
│       └── Loan.java
├── src/main/resources/
│   ├── application.properties          ← 설정 파일
│   └── templates/                      ← HTML 화면
│       ├── dashboard.html
│       ├── fragments/header.html
│       ├── book/   (list, form, detail)
│       ├── member/ (list, form, detail)
│       └── loan/   (list, form, detail)
├── pom.xml                             ← Maven 의존성 설정
└── mvnw.cmd                            ← Maven Wrapper (빌드 도구)
```

---

## 화면 미리보기

| 화면 | 설명 |
|------|------|
| `/` | 대시보드 - 전체 현황 |
| `/books` | 도서 목록 및 검색 |
| `/books/new` | 도서 등록 |
| `/members` | 회원 목록 및 검색 |
| `/members/new` | 회원 등록 |
| `/loans` | 대출 목록 |
| `/loans/new` | 대출 처리 |

---

## 문제 해결

### MySQL 연결 오류
- MySQL 서비스가 실행 중인지 확인하세요.
- `book_db` 데이터베이스가 생성되었는지 확인하세요.
- DB 비밀번호가 올바른지 확인하세요.

### 포트 충돌
8080 포트를 다른 프로그램이 사용 중이면 `application.properties`에서 포트를 변경하세요:
```properties
server.port=9090
```
