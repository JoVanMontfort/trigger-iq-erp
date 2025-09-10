package damnosol.triggeriq.erp.infra.parser;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

@Component
public class ExcelParserAdapter {

    private static final Logger log = LoggerFactory.getLogger(ExcelParserAdapter.class);

    /**
     * Parse an Excel file and log rows.
     *
     * @param fileName File name relative to storage path
     */
    public void parseExcel(String fileName) {
        try {
            File file = Paths.get("./data/uploads", fileName).toFile();
            log.info("Parsing Excel file: {}", file.getAbsolutePath());

            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = WorkbookFactory.create(fis)) {

                Sheet sheet = workbook.getSheetAt(0); // first sheet
                for (Row row : sheet) {
                    StringBuilder sb = new StringBuilder();
                    for (Cell cell : row) {
                        sb.append(getCellValue(cell)).append(" | ");
                    }
                    log.info("Row: {}", sb);
                }
            }

        } catch (Exception e) {
            log.error("Failed to parse Excel file: {}", fileName, e);
        }
    }

    private Object getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}