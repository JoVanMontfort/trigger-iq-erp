package damnosol.triggeriq.erp.core.domain.model;

import java.time.LocalDateTime;

public record Document(
        Long id,
        String fileName,
        String fileType,
        byte[] content,
        LocalDateTime uploadedAt,
        Long productId,   // optional FK
        Long inventoryId  // optional FK
) {
}