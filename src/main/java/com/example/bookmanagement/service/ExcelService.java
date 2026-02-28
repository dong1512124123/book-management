package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.Loan;
import com.example.bookmanagement.entity.LoginHistory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

// 엑셀(xlsx) 생성 서비스
@Service
public class ExcelService {

    // 도서 목록 엑셀 생성
    public byte[] generateBookListExcel(List<Book> books, Set<Long> borrowedBookIds) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("도서 목록");

            // 헤더 스타일
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            String[] headers = {"번호", "제목", "저자", "출판사", "ISBN", "상태"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum);

                createCell(row, 0, rowNum, cellStyle);
                createCell(row, 1, book.getTitle(), cellStyle);
                createCell(row, 2, book.getAuthor() != null ? book.getAuthor() : "", cellStyle);
                createCell(row, 3, book.getPublisher() != null ? book.getPublisher() : "", cellStyle);
                createCell(row, 4, book.getIsbn() != null ? book.getIsbn() : "", cellStyle);
                createCell(row, 5, borrowedBookIds.contains(book.getId()) ? "대출 중" : "비치 중", cellStyle);

                rowNum++;
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 최소 너비 보장
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 특정 도서의 대출 이력 엑셀 생성
    public byte[] generateLoanHistoryExcel(Book book, List<Loan> loans) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("대출 이력");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            // 도서 정보 행
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("도서: " + book.getTitle() + " / " + book.getAuthor());
            titleCell.setCellStyle(titleStyle);

            // 헤더 행
            Row headerRow = sheet.createRow(2);
            String[] headers = {"번호", "대여자", "대출일", "반납예정일", "반납일", "상태"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            int rowNum = 3;
            int num = 1;
            for (Loan loan : loans) {
                Row row = sheet.createRow(rowNum);

                createCell(row, 0, num, cellStyle);
                createCell(row, 1, loan.getMember() != null ? loan.getMember().getName() : "", cellStyle);
                createCell(row, 2, loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : "", cellStyle);
                createCell(row, 3, loan.getReturnDueDate() != null ? loan.getReturnDueDate().toString() : "", cellStyle);
                createCell(row, 4, loan.getReturnedDate() != null ? loan.getReturnedDate().toString() : "-", cellStyle);

                String status;
                if ("RETURNED".equals(loan.getStatus())) status = "반납 완료";
                else if ("RETURN_REQUESTED".equals(loan.getStatus())) status = "반납 대기";
                else if ("APPROVED".equals(loan.getStatus())) status = loan.isOverdue() ? "연체" : "대출 중";
                else if ("REQUESTED".equals(loan.getStatus())) status = "승인 대기";
                else status = loan.getStatus();
                createCell(row, 5, status, cellStyle);

                rowNum++;
                num++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 대출/반납 목록 엑셀 생성
    public byte[] generateLoanListExcel(List<Loan> loans) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("대출 반납 목록");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"번호", "도서명", "저자", "출판사", "회원명", "대출일", "반납기한", "반납일", "상태"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Loan loan : loans) {
                Row row = sheet.createRow(rowNum);

                createCell(row, 0, rowNum, cellStyle);
                createCell(row, 1, loan.getBook() != null ? loan.getBook().getTitle() : "", cellStyle);
                createCell(row, 2, loan.getBook() != null ? loan.getBook().getAuthor() : "", cellStyle);
                createCell(row, 3, loan.getBook() != null ? loan.getBook().getPublisher() : "", cellStyle);
                createCell(row, 4, loan.getMember() != null ? loan.getMember().getName() : "", cellStyle);
                createCell(row, 5, loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : "", cellStyle);
                createCell(row, 6, loan.getReturnDueDate() != null ? loan.getReturnDueDate().toString() : "", cellStyle);
                createCell(row, 7, loan.getReturnedDate() != null ? loan.getReturnedDate().toString() : "-", cellStyle);

                String status;
                if ("RETURNED".equals(loan.getStatus())) status = "반납 완료";
                else if ("RETURN_REQUESTED".equals(loan.getStatus())) status = "반납 대기";
                else if ("APPROVED".equals(loan.getStatus())) status = loan.isOverdue() ? "연체" : "대출 중";
                else if ("REQUESTED".equals(loan.getStatus())) status = "승인 대기";
                else status = loan.getStatus();
                createCell(row, 8, status, cellStyle);

                rowNum++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 로그인 이력 엑셀 생성
    public byte[] generateLoginHistoryExcel(List<LoginHistory> histories) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("로그인 이력");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            String[] headers = {"번호", "유형", "아이디", "로그인 시각", "IP 주소"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            int rowNum = 1;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (LoginHistory h : histories) {
                Row row = sheet.createRow(rowNum);

                createCell(row, 0, rowNum, cellStyle);
                createCell(row, 1, "ADMIN".equals(h.getUserType()) ? "관리자" : "사용자", cellStyle);
                createCell(row, 2, h.getUsername(), cellStyle);
                createCell(row, 3, h.getLoginTime() != null ? h.getLoginTime().format(dtf) : "", cellStyle);
                createCell(row, 4, h.getIpAddress() != null ? h.getIpAddress() : "", cellStyle);

                rowNum++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // --- 스타일 헬퍼 ---

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        style.setFont(font);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int col, int value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
