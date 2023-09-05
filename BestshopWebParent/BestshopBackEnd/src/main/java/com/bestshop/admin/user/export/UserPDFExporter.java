package com.bestshop.admin.user.export;

import com.bestshop.admin.user.AbstractExporter;
import com.bestshop.common.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class UserPDFExporter extends AbstractExporter {

    Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
    Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);


    public void export(List<User> listUsers, HttpServletResponse response) throws IOException, DocumentException {
        super.setResponseHeader(response, "application/pdf", ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph paragraph = new Paragraph("List of Users", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);//6 Columns table created
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});

        addTableHeader(table);//Add header to table

        addTableRows(table, listUsers);//Add user data to the table

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table){
            String[] header = {"ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(BaseColor.BLUE);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            for(String columnHeader : header){
                headerCell.setPadding(5);
                headerCell.setPhrase(new Phrase(columnHeader, fontHeader));
                table.addCell(headerCell);
            }

        }

    private void addTableRows(PdfPTable table, List<User> listUsers) {
        // Preenche as linhas da tabela com os dados dos usuários
        for (User user : listUsers) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.isEnabled()));
        }
    }

}
