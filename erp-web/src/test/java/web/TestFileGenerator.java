package web;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileGenerator {

    public static Path generateExcelFile(String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("SKU");
        header.createCell(2).setCellValue("Price");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Test Product");
        dataRow.createCell(1).setCellValue("SKU001");
        dataRow.createCell(2).setCellValue(19.99);

        Path tempFile = Files.createTempFile(fileName, ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            workbook.write(fos);
        }
        workbook.close();
        return tempFile;
    }

    public static Path generatePdfFile(String fileName) throws IOException {
        Path tempFile = Files.createTempFile(fileName, ".pdf");
        PdfWriter writer = new PdfWriter(tempFile.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Sample PDF content for testing."));
        document.close();

        return tempFile;
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        Path excel = generateExcelFile("test_inventory");
        Path pdf = generatePdfFile("test_inventory");
        System.out.println("Generated XLSX: " + excel.toAbsolutePath());
        System.out.println("Generated PDF: " + pdf.toAbsolutePath());
    }
}