package damnosol.triggeriq.erp.core.application.port.in;

import damnosol.triggeriq.erp.core.domain.model.Document;

import java.io.File;
import java.util.List;

/**
 * Use case port for importing and attaching documents to ERP entities.
 * Supports Excel, PDF, and generic file imports.
 */
public interface FileImportUseCase {

    /**
     * Import an Excel file and return the parsed documents.
     *
     * @param excelFile Excel file to import
     * @return list of created documents
     */
    List<Document> importExcel(File excelFile);

    /**
     * Import a PDF file and return the parsed documents.
     *
     * @param pdfFile PDF file to import
     * @return list of created documents
     */
    List<Document> importPdf(File pdfFile);

    /**
     * Attach a list of files as documents to a product.
     *
     * @param productId ID of the product
     * @param files     files to attach
     * @return list of attached documents
     */
    List<Document> attachDocumentsToProduct(Long productId, List<File> files);

    /**
     * Attach a list of files as documents to an inventory item.
     *
     * @param inventoryId ID of the inventory
     * @param files       files to attach
     * @return list of attached documents
     */
    List<Document> attachDocumentsToInventory(Long inventoryId, List<File> files);

    /**
     * Import a file of arbitrary type and process it accordingly.
     * Typically used for generic upload endpoints where the type is known from file extension.
     *
     * @param filePath absolute or relative path to the file
     * @param fileType type of file, e.g., "xlsx" or "pdf"
     * @return status message describing the outcome
     */
    String importFile(String filePath, String fileType);
}