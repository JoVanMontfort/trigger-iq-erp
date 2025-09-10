package damnosol.triggeriq.erp.infra.persistence;

import damnosol.triggeriq.erp.core.domain.model.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcInventoryRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcInventoryRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertInventory;

    public JdbcInventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertInventory = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("inventory")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Inventory> inventoryMapper = (rs, rowNum) ->
            new Inventory(
                    rs.getLong("id"),
                    rs.getLong("product_id"),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold")
            );

    public Inventory save(Inventory inventory) {
        log.info("Saving inventory: {}", inventory);
        Map<String, Object> params = Map.of(
                "product_id", inventory.product(),
                "quantity", inventory.quantity(),
                "min_threshold", inventory.minThreshold()
        );
        Number key = insertInventory.executeAndReturnKey(params);
        Inventory saved = new Inventory(
                key.longValue(),
                inventory.product(),
                inventory.quantity(),
                inventory.minThreshold()
        );
        log.info("Saved inventory: {}", saved);
        return saved;
    }

    public void update(Inventory inventory) {
        log.info("Updating inventory: {}", inventory);
        jdbcTemplate.update(
                "UPDATE inventory SET quantity = ?, min_threshold = ? WHERE id = ?",
                inventory.quantity(),
                inventory.minThreshold(),
                inventory.id()
        );
        log.info("Inventory updated: {}", inventory);
    }

    public void delete(Long id) {
        log.info("Deleting inventory with ID: {}", id);
        jdbcTemplate.update("DELETE FROM inventory WHERE id = ?", id);
        log.info("Inventory deleted with ID: {}", id);
    }

    public Optional<Inventory> findById(Long id) {
        log.info("Fetching inventory by ID: {}", id);
        List<Inventory> results = jdbcTemplate.query(
                "SELECT id, product_id, quantity, min_threshold FROM inventory WHERE id = ?",
                inventoryMapper,
                id
        );
        Optional<Inventory> result = results.stream().findFirst();
        if (result.isEmpty()) {
            log.warn("No inventory found with ID: {}", id);
        }
        return result;
    }
}