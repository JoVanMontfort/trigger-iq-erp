package damnosol.triggeriq.erp.core.application.port.in;

import damnosol.triggeriq.erp.core.domain.model.Document;

import java.io.File;
import java.util.List;

public interface FileImportUseCase {

    List<Document> importExcel(File excelFile);

    List<Document> importPdf(File pdfFile);

    List<Document> attachDocumentsToProduct(Long productId, List<File> files);

    List<Document> attachDocumentsToInventory(Long inventoryId, List<File> files);
}