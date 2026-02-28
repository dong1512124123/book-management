package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.LoginHistory;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

// PDF 생성 서비스
// OpenPDF 라이브러리를 사용하여 도서 목록 PDF를 생성
@Service
public class PdfService {

    // 한글 폰트 (classpath 리소스에서 로드, Windows/Linux 모두 동작)
    private BaseFont getKoreanFont() throws Exception {
        // classpath의 폰트 파일을 임시 파일로 추출하여 사용
        ClassPathResource resource = new ClassPathResource("fonts/malgun.ttf");
        File tempFont = File.createTempFile("malgun", ".ttf");
        tempFont.deleteOnExit();
        try (InputStream is = resource.getInputStream();
             FileOutputStream fos = new FileOutputStream(tempFont)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }
        return BaseFont.createFont(tempFont.getAbsolutePath(),
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    // 도서 목록 PDF 생성
    public byte[] generateBookListPdf(List<Book> books, Set<Long> borrowedBookIds) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        BaseFont bf = getKoreanFont();
        Font titleFont = new Font(bf, 18, Font.BOLD, new Color(30, 86, 160));
        Font headerFont = new Font(bf, 9, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(bf, 8, Font.NORMAL, Color.DARK_GRAY);
        Font dateFont = new Font(bf, 8, Font.NORMAL, Color.GRAY);

        // 제목
        Paragraph title = new Paragraph("LibraryGO - 도서 목록", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        // 날짜
        Paragraph datePara = new Paragraph("출력일: " + LocalDate.now(), dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(15);
        document.add(datePara);

        // 테이블 (5열: 번호, 제목, 저자, 출판사, 상태)
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 4f, 2f, 2f, 1.5f});

        // 헤더 스타일
        Color headerBg = new Color(30, 86, 160);
        String[] headers = {"번호", "제목", "저자", "출판사", "상태"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // 데이터
        int num = 1;
        Color altBg = new Color(248, 248, 252);
        for (Book book : books) {
            Color rowBg = (num % 2 == 0) ? altBg : Color.WHITE;

            PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(num), cellFont));
            numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            numCell.setBackgroundColor(rowBg);
            numCell.setPadding(5);
            table.addCell(numCell);

            PdfPCell titleCell = new PdfPCell(new Phrase(book.getTitle(), cellFont));
            titleCell.setBackgroundColor(rowBg);
            titleCell.setPadding(5);
            table.addCell(titleCell);

            PdfPCell authorCell = new PdfPCell(new Phrase(
                    book.getAuthor() != null ? book.getAuthor() : "", cellFont));
            authorCell.setBackgroundColor(rowBg);
            authorCell.setPadding(5);
            table.addCell(authorCell);

            PdfPCell pubCell = new PdfPCell(new Phrase(
                    book.getPublisher() != null ? book.getPublisher() : "", cellFont));
            pubCell.setBackgroundColor(rowBg);
            pubCell.setPadding(5);
            table.addCell(pubCell);

            String status = borrowedBookIds.contains(book.getId()) ? "대출 중" : "비치 중";
            PdfPCell statusCell = new PdfPCell(new Phrase(status, cellFont));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setBackgroundColor(rowBg);
            statusCell.setPadding(5);
            table.addCell(statusCell);

            num++;
        }

        document.add(table);

        // 하단 요약
        Paragraph summary = new Paragraph(
                "총 " + books.size() + "권 (비치 중: " + (books.size() - borrowedBookIds.size()) + "권, 대출 중: " + borrowedBookIds.size() + "권)",
                new Font(bf, 9, Font.NORMAL, Color.GRAY));
        summary.setSpacingBefore(10);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        document.close();
        return out.toByteArray();
    }

    // 특정 도서의 대출 이력 PDF 생성
    public byte[] generateLoanHistoryPdf(Book book, List<Loan> loans) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        BaseFont bf = getKoreanFont();
        Font titleFont = new Font(bf, 16, Font.BOLD, new Color(30, 86, 160));
        Font subTitleFont = new Font(bf, 11, Font.NORMAL, Color.DARK_GRAY);
        Font headerFont = new Font(bf, 9, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(bf, 8, Font.NORMAL, Color.DARK_GRAY);
        Font dateFont = new Font(bf, 8, Font.NORMAL, Color.GRAY);

        // 제목
        Paragraph title = new Paragraph("대출 이력 카드", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        // 도서 정보
        Paragraph bookInfo = new Paragraph(book.getTitle() + " / " + book.getAuthor(), subTitleFont);
        bookInfo.setAlignment(Element.ALIGN_CENTER);
        bookInfo.setSpacingAfter(3);
        document.add(bookInfo);

        // 출력일
        Paragraph datePara = new Paragraph("출력일: " + LocalDate.now(), dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(15);
        document.add(datePara);

        // 테이블 (5열: 번호, 대여자, 대출일, 반납예정일, 반납일, 상태)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2f, 2f, 2f, 2f, 1.5f});

        Color headerBg = new Color(30, 86, 160);
        String[] headers = {"번호", "대여자", "대출일", "반납예정일", "반납일", "상태"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        int num = 1;
        Color altBg = new Color(245, 248, 252);
        for (Loan loan : loans) {
            Color rowBg = (num % 2 == 0) ? altBg : Color.WHITE;

            PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(num), cellFont));
            numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            numCell.setBackgroundColor(rowBg);
            numCell.setPadding(5);
            table.addCell(numCell);

            PdfPCell nameCell = new PdfPCell(new Phrase(
                    loan.getMember() != null ? loan.getMember().getName() : "", cellFont));
            nameCell.setBackgroundColor(rowBg);
            nameCell.setPadding(5);
            table.addCell(nameCell);

            PdfPCell borrowCell = new PdfPCell(new Phrase(
                    loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : "", cellFont));
            borrowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            borrowCell.setBackgroundColor(rowBg);
            borrowCell.setPadding(5);
            table.addCell(borrowCell);

            PdfPCell dueCell = new PdfPCell(new Phrase(
                    loan.getReturnDueDate() != null ? loan.getReturnDueDate().toString() : "", cellFont));
            dueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dueCell.setBackgroundColor(rowBg);
            dueCell.setPadding(5);
            table.addCell(dueCell);

            PdfPCell returnCell = new PdfPCell(new Phrase(
                    loan.getReturnedDate() != null ? loan.getReturnedDate().toString() : "-", cellFont));
            returnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            returnCell.setBackgroundColor(rowBg);
            returnCell.setPadding(5);
            table.addCell(returnCell);

            String status;
            if ("RETURNED".equals(loan.getStatus())) status = "반납 완료";
            else if ("RETURN_REQUESTED".equals(loan.getStatus())) status = "반납 대기";
            else if ("APPROVED".equals(loan.getStatus())) status = loan.isOverdue() ? "연체" : "대출 중";
            else if ("REQUESTED".equals(loan.getStatus())) status = "승인 대기";
            else status = loan.getStatus();

            PdfPCell statusCell = new PdfPCell(new Phrase(status, cellFont));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setBackgroundColor(rowBg);
            statusCell.setPadding(5);
            table.addCell(statusCell);

            num++;
        }

        document.add(table);

        // 하단 요약
        Paragraph summary = new Paragraph("총 " + loans.size() + "건",
                new Font(bf, 9, Font.NORMAL, Color.GRAY));
        summary.setSpacingBefore(10);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        document.close();
        return out.toByteArray();
    }

    // 대출/반납 목록 PDF 생성
    public byte[] generateLoanListPdf(List<Loan> loans) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
        PdfWriter.getInstance(document, out);
        document.open();

        BaseFont bf = getKoreanFont();
        Font titleFont = new Font(bf, 16, Font.BOLD, new Color(30, 86, 160));
        Font headerFont = new Font(bf, 8, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(bf, 7, Font.NORMAL, Color.DARK_GRAY);
        Font dateFont = new Font(bf, 8, Font.NORMAL, Color.GRAY);

        Paragraph title = new Paragraph("LibraryGO - 대출/반납 목록", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        Paragraph datePara = new Paragraph("출력일: " + LocalDate.now(), dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(15);
        document.add(datePara);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.6f, 3f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.2f});

        Color headerBg = new Color(30, 86, 160);
        String[] headers = {"번호", "도서명", "저자", "회원명", "대출일", "반납기한", "반납일", "상태"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        int num = 1;
        Color altBg = new Color(245, 248, 252);
        for (Loan loan : loans) {
            Color rowBg = (num % 2 == 0) ? altBg : Color.WHITE;

            PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(num), cellFont));
            numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            numCell.setBackgroundColor(rowBg);
            numCell.setPadding(4);
            table.addCell(numCell);

            PdfPCell bookCell = new PdfPCell(new Phrase(
                    loan.getBook() != null ? loan.getBook().getTitle() : "", cellFont));
            bookCell.setBackgroundColor(rowBg);
            bookCell.setPadding(4);
            table.addCell(bookCell);

            PdfPCell authorCell = new PdfPCell(new Phrase(
                    loan.getBook() != null ? loan.getBook().getAuthor() : "", cellFont));
            authorCell.setBackgroundColor(rowBg);
            authorCell.setPadding(4);
            table.addCell(authorCell);

            PdfPCell memberCell = new PdfPCell(new Phrase(
                    loan.getMember() != null ? loan.getMember().getName() : "", cellFont));
            memberCell.setBackgroundColor(rowBg);
            memberCell.setPadding(4);
            table.addCell(memberCell);

            PdfPCell borrowCell = new PdfPCell(new Phrase(
                    loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : "", cellFont));
            borrowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            borrowCell.setBackgroundColor(rowBg);
            borrowCell.setPadding(4);
            table.addCell(borrowCell);

            PdfPCell dueCell = new PdfPCell(new Phrase(
                    loan.getReturnDueDate() != null ? loan.getReturnDueDate().toString() : "", cellFont));
            dueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dueCell.setBackgroundColor(rowBg);
            dueCell.setPadding(4);
            table.addCell(dueCell);

            PdfPCell returnCell = new PdfPCell(new Phrase(
                    loan.getReturnedDate() != null ? loan.getReturnedDate().toString() : "-", cellFont));
            returnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            returnCell.setBackgroundColor(rowBg);
            returnCell.setPadding(4);
            table.addCell(returnCell);

            String status;
            if ("RETURNED".equals(loan.getStatus())) status = "반납 완료";
            else if ("RETURN_REQUESTED".equals(loan.getStatus())) status = "반납 대기";
            else if ("APPROVED".equals(loan.getStatus())) status = loan.isOverdue() ? "연체" : "대출 중";
            else if ("REQUESTED".equals(loan.getStatus())) status = "승인 대기";
            else status = loan.getStatus();

            PdfPCell statusCell = new PdfPCell(new Phrase(status, cellFont));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setBackgroundColor(rowBg);
            statusCell.setPadding(4);
            table.addCell(statusCell);

            num++;
        }

        document.add(table);

        Paragraph summary = new Paragraph("총 " + loans.size() + "건",
                new Font(bf, 9, Font.NORMAL, Color.GRAY));
        summary.setSpacingBefore(10);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        document.close();
        return out.toByteArray();
    }

    // 로그인 이력 PDF 생성
    public byte[] generateLoginHistoryPdf(List<LoginHistory> histories) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        BaseFont bf = getKoreanFont();
        Font titleFont = new Font(bf, 16, Font.BOLD, new Color(30, 86, 160));
        Font headerFont = new Font(bf, 9, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(bf, 8, Font.NORMAL, Color.DARK_GRAY);
        Font dateFont = new Font(bf, 8, Font.NORMAL, Color.GRAY);

        // 제목
        Paragraph title = new Paragraph("LibraryGO - 로그인 이력", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        // 출력일
        Paragraph datePara = new Paragraph("출력일: " + LocalDate.now(), dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(15);
        document.add(datePara);

        // 테이블 (4열: 번호, 유형, 아이디, 로그인 시각, IP 주소)
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 1.2f, 2.5f, 3f, 2.5f});

        Color headerBg = new Color(30, 86, 160);
        String[] headers = {"번호", "유형", "아이디", "로그인 시각", "IP 주소"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        int num = 1;
        Color altBg = new Color(245, 248, 252);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (LoginHistory h : histories) {
            Color rowBg = (num % 2 == 0) ? altBg : Color.WHITE;

            PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(num), cellFont));
            numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            numCell.setBackgroundColor(rowBg);
            numCell.setPadding(5);
            table.addCell(numCell);

            String userTypeText = "ADMIN".equals(h.getUserType()) ? "관리자" : "사용자";
            PdfPCell typeCell = new PdfPCell(new Phrase(userTypeText, cellFont));
            typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            typeCell.setBackgroundColor(rowBg);
            typeCell.setPadding(5);
            table.addCell(typeCell);

            PdfPCell usernameCell = new PdfPCell(new Phrase(h.getUsername(), cellFont));
            usernameCell.setBackgroundColor(rowBg);
            usernameCell.setPadding(5);
            table.addCell(usernameCell);

            PdfPCell timeCell = new PdfPCell(new Phrase(
                    h.getLoginTime() != null ? h.getLoginTime().format(dtf) : "", cellFont));
            timeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            timeCell.setBackgroundColor(rowBg);
            timeCell.setPadding(5);
            table.addCell(timeCell);

            PdfPCell ipCell = new PdfPCell(new Phrase(
                    h.getIpAddress() != null ? h.getIpAddress() : "", cellFont));
            ipCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ipCell.setBackgroundColor(rowBg);
            ipCell.setPadding(5);
            table.addCell(ipCell);

            num++;
        }

        document.add(table);

        Paragraph summary = new Paragraph("총 " + histories.size() + "건",
                new Font(bf, 9, Font.NORMAL, Color.GRAY));
        summary.setSpacingBefore(10);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        document.close();
        return out.toByteArray();
    }
}
