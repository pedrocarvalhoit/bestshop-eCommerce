package com.bestshop.admin.user.export;

import com.bestshop.admin.AbstractExporter;
import com.bestshop.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    public void export (List<User> listUsers, HttpServletResponse response, String moduleName) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx", moduleName);

        Sheet sheet = workbook.createSheet("Users");

        String[] excelHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < excelHeader.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for(User user : listUsers){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getEmail());
            row.createCell(2).setCellValue(user.getFirstName());
            row.createCell(3).setCellValue(user.getLastName());
            row.createCell(4).setCellValue(user.getRoles().toString());
            row.createCell(5).setCellValue(user.isEbabled());
        }

        for (int i = 0; i < excelHeader.length; i++){
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();

    }
}
