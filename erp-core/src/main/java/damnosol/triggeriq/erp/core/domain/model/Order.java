package damnosol.triggeriq.erp.core.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        Long id,
        Customer customer,
        List<Product> products,
        LocalDateTime orderDate
) {
}