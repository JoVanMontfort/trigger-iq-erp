package damnosol.triggeriq.erp.core.application.service;

import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.core.application.port.out.ExcelParserPort;
import damnosol.triggeriq.erp.core.application.port.out.FileStoragePort;
import damnosol.triggeriq.erp.core.application.port.out.PdfParserPort;
import damnosol.triggeriq.erp.core.domain.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class FileImportService implements FileImportUseCase {

    private static final Logger log = LoggerFactory.getLogger(FileImportService.class);

    private final FileStoragePort fileStorage;
    private final ExcelParserPort excelParser;
    private final PdfParserPort pdfParser;

    public FileImportService(FileStoragePort fileStorage,
                             ExcelParserPort excelParser,
                             PdfParserPort pdfParser) {
        this.fileStorage = fileStorage;
        this.excelParser = excelParser;
        this.pdfParser = pdfParser;
    }

    @Override
    public List<Document> importExcel(File excelFile) {
        try {
            log.info("Importing Excel file: {}", excelFile.getAbsolutePath());
            excelParser.parseExcel(excelFile.getAbsolutePath());
            return fileStorage.storeFiles(List.of(excelFile), null, null);
        } catch (Exception e) {
            log.error("Failed to import Excel file", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> importPdf(File pdfFile) {
        try {
            log.info("Importing PDF file: {}", pdfFile.getAbsolutePath());
            pdfParser.parsePdf(pdfFile.getAbsolutePath());
            return fileStorage.storeFiles(List.of(pdfFile), null, null);
        } catch (Exception e) {
            log.error("Failed to import PDF file", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> attachDocumentsToProduct(Long productId, List<File> files) {
        log.info("Attaching {} files to product {}", files.size(), productId);
        return fileStorage.storeFiles(files, productId, null);
    }

    @Override
    public List<Document> attachDocumentsToInventory(Long inventoryId, List<File> files) {
        log.info("Attaching {} files to inventory {}", files.size(), inventoryId);
        return fileStorage.storeFiles(files, null, inventoryId);
    }

    @Override
    public String importFile(String filePath, String fileType) {
        File file = new File(filePath);
        if (!file.exists()) {
            String msg = "File not found: " + filePath;
            log.warn(msg);
            return msg;
        }

        switch (fileType.toLowerCase()) {
            case "xlsx", "xls" -> {
                excelParser.parseExcel(filePath);
                fileStorage.storeFiles(List.of(file), null, null);
                log.info("Excel file imported successfully: {}", filePath);
                return "Excel file imported successfully: " + file.getName();
            }
            case "pdf" -> {
                pdfParser.parsePdf(filePath);
                fileStorage.storeFiles(List.of(file), null, null);
                log.info("PDF file imported successfully: {}", filePath);
                return "PDF file imported successfully: " + file.getName();
            }
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }
}