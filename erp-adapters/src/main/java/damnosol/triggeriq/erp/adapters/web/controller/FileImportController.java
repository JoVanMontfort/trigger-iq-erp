package damnosol.triggeriq.erp.adapters.web.controller;

import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.core.domain.model.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileImportController {

    private final FileImportUseCase fileImportUseCase;

    public FileImportController(FileImportUseCase fileImportUseCase) {
        this.fileImportUseCase = fileImportUseCase;
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
}