package damnosol.triggeriq.erp.core.application.port.out;


import damnosol.triggeriq.erp.core.domain.model.Document;

import java.io.File;
import java.util.List;

public interface FileStoragePort {
    List<Document> storeFiles(List<File> files, Long productId, Long inventoryId);
}