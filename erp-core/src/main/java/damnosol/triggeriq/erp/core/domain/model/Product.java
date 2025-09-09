package damnosol.triggeriq.erp.core.domain.model;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        String sku,
        BigDecimal price
) {
}