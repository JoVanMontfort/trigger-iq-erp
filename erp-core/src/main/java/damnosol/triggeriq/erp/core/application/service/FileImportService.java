package damnosol.triggeriq.erp.core.application.service;

import damnosol.triggeriq.erp.core.application.port.in.FileImportUseCase;
import damnosol.triggeriq.erp.core.application.port.out.FileStoragePort;
import damnosol.triggeriq.erp.core.domain.model.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileImportService implements FileImportUseCase {

    private final FileStoragePort fileStorage;

    public FileImportService(FileStoragePort fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public List<Document> importExcel(File excelFile) {
        // parse Excel and generate documents
        // could use Apache POI or similar
        return fileStorage.storeFiles(List.of(excelFile), null, null);
    }

    @Override
    public List<Document> importPdf(File pdfFile) {
        // parse PDF if needed, or just store
        return fileStorage.storeFiles(List.of(pdfFile), null, null);
    }

    @Override
    public List<Document> attachDocumentsToProduct(Long productId, List<File> files) {
        return fileStorage.storeFiles(files, productId, null);
    }

    @Override
    public List<Document> attachDocumentsToInventory(Long inventoryId, List<File> files) {
        return fileStorage.storeFiles(files, null, inventoryId);
    }
}