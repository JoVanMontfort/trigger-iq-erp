package damnosol.triggeriq.erp.adapters.web.controller;

import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.core.domain.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/import")
public class FileImportController {

    private static final Logger log = LoggerFactory.getLogger(FileImportController.class);

    private final FileImportUseCase fileImportUseCase;

    public FileImportController(FileImportUseCase fileImportUseCase) {
        this.fileImportUseCase = fileImportUseCase;
    }

    // -------------------- Helper Methods --------------------
    private List<File> convertMultipartToTempFiles(List<MultipartFile> files) {
        return files.stream().map(f -> {
            try {
                File tmp = File.createTempFile("upload-", f.getOriginalFilename());
                f.transferTo(tmp);
                log.info("Created temporary file: {}", tmp.getAbsolutePath());
                return tmp;
            } catch (IOException e) {
                log.error("Failed to create temporary file for: {}", f.getOriginalFilename(), e);
                throw new RuntimeException("Failed to process uploaded file: " + f.getOriginalFilename(), e);
            }
        }).collect(Collectors.toList());
    }

    private String getFileType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".xlsx") || name.endsWith(".xls")) return "xlsx";
        if (name.endsWith(".pdf")) return "pdf";
        return "unsupported";
    }

    private void cleanupTempFiles(List<File> files) {
        for (File f : files) {
            if (f.exists() && !f.delete()) {
                log.warn("Failed to delete temporary file: {}", f.getAbsolutePath());
            }
        }
    }

    // -------------------- Upload Endpoints --------------------
    @PostMapping("/product/{productId}")
    public ResponseEntity<List<Document>> uploadForProduct(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        log.info("Uploading {} files for product {}", files.size(), productId);
        List<File> tempFiles = convertMultipartToTempFiles(files);

        try {
            List<Document> documents = fileImportUseCase.attachDocumentsToProduct(productId, tempFiles);
            log.info("Attached {} documents to product {}", documents.size(), productId);
            return ResponseEntity.ok(documents);
        } finally {
            cleanupTempFiles(tempFiles);
        }
    }

    @PostMapping("/inventory/{inventoryId}")
    public ResponseEntity<List<Document>> uploadForInventory(
            @PathVariable Long inventoryId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        log.info("Uploading {} files for inventory {}", files.size(), inventoryId);
        List<File> tempFiles = convertMultipartToTempFiles(files);

        try {
            List<Document> documents = fileImportUseCase.attachDocumentsToInventory(inventoryId, tempFiles);
            log.info("Attached {} documents to inventory {}", documents.size(), inventoryId);
            return ResponseEntity.ok(documents);
        } finally {
            cleanupTempFiles(tempFiles);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Uploading single file: {}", file.getOriginalFilename());
        File tmpFile = null;

        try {
            tmpFile = File.createTempFile("upload-", Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(tmpFile);
            log.info("Temporary file created at {}", tmpFile.getAbsolutePath());

            String fileType = getFileType(tmpFile);
            if ("unsupported".equals(fileType)) {
                log.warn("Unsupported file type attempted: {}", tmpFile.getName());
                return ResponseEntity.badRequest().body("Unsupported file type: " + tmpFile.getName());
            }

            String result = fileImportUseCase.importFile(tmpFile.getAbsolutePath(), fileType);
            log.info("File processed successfully: {}", tmpFile.getName());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to upload or process file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        } finally {
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete()) {
                log.warn("Failed to delete temporary file: {}", tmpFile.getAbsolutePath());
            }
        }
    }
}