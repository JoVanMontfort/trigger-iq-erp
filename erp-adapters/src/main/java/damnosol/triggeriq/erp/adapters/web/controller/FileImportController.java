package damnosol.triggeriq.erp.adapters.web.controller;

import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.core.domain.model.Document;
import damnosol.triggeriq.erp.infra.parser.ExcelParserAdapter;
import damnosol.triggeriq.erp.infra.parser.PdfParserAdapter;
import damnosol.triggeriq.erp.infra.storage.FileSystemStorageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileImportController {

    private static final Logger log = LoggerFactory.getLogger(FileImportController.class);

    private final FileImportUseCase fileImportUseCase;

    private final FileSystemStorageAdapter storageAdapter;
    private final ExcelParserAdapter excelParser;
    private final PdfParserAdapter pdfParser;

    public FileImportController(FileImportUseCase fileImportUseCase,
                                FileSystemStorageAdapter storageAdapter,
                                ExcelParserAdapter excelParser,
                                PdfParserAdapter pdfParser) {
        this.fileImportUseCase = fileImportUseCase;
        this.storageAdapter = storageAdapter;
        this.excelParser = excelParser;
        this.pdfParser = pdfParser;
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<List<Document>> uploadForProduct(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        List<File> tempFiles = files.stream().map(f -> {
            try {
                File tmp = File.createTempFile("upload-", f.getOriginalFilename());
                f.transferTo(tmp);
                return tmp;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        List<Document> documents = fileImportUseCase.attachDocumentsToProduct(productId, tempFiles);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/inventory/{inventoryId}")
    public ResponseEntity<List<Document>> uploadForInventory(
            @PathVariable Long inventoryId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        List<File> tempFiles = files.stream().map(f -> {
            try {
                File tmp = File.createTempFile("upload-", f.getOriginalFilename());
                f.transferTo(tmp);
                return tmp;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        List<Document> documents = fileImportUseCase.attachDocumentsToInventory(inventoryId, tempFiles);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Uploading file: {}", file.getOriginalFilename());
            Path savedPath = storageAdapter.saveFile(file.getOriginalFilename(), file.getBytes());

            String filename = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                excelParser.parseExcel(savedPath.getFileName().toString());
                return ResponseEntity.ok("Excel file imported successfully: " + savedPath);
            } else if (filename.endsWith(".pdf")) {
                pdfParser.parsePdf(savedPath.getFileName().toString());
                return ResponseEntity.ok("PDF file imported successfully: " + savedPath);
            } else {
                return ResponseEntity.badRequest().body("Unsupported file type: " + filename);
            }
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }
}