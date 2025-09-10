package damnosol.triggeriq.erp.infra.persistence;

import damnosol.triggeriq.erp.core.domain.model.Inventory;
import damnosol.triggeriq.erp.core.domain.model.Product;
import damnosol.triggeriq.erp.core.domain.repository.InventoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcInventoryRepository implements InventoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcInventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Product> productMapper = (rs, rowNum) ->
            new Product(
                    rs.getLong("product_id"),
                    rs.getString("name"),
                    rs.getString("sku"),
                    rs.getBigDecimal("price")
            );

    private final RowMapper<Inventory> inventoryMapper = (rs, rowNum) ->
            new Inventory(
                    rs.getLong("inventory_id"),
                    new Product(
                            rs.getLong("product_id"),
                            rs.getString("name"),
                            rs.getString("sku"),
                            rs.getBigDecimal("price")
                    ),
                    rs.getInt("quantity"),
                    rs.getInt("min_threshold")
            );

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.id() == null) {
            // Insert new record
            jdbcTemplate.update(
                    "INSERT INTO inventory (product_id, quantity, min_threshold) VALUES (?, ?, ?)",
                    inventory.product().id(),
                    inventory.quantity(),
                    inventory.minThreshold()
            );
            // Ideally retrieve generated key for id
            // For simplicity, return inventory as is
            return inventory;
        } else {
            // Update existing record
            jdbcTemplate.update(
                    "UPDATE inventory SET quantity = ?, min_threshold = ? WHERE inventory_id = ?",
                    inventory.quantity(),
                    inventory.minThreshold(),
                    inventory.id()
            );
            return inventory;
        }
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        List<Inventory> results = jdbcTemplate.query(
                "SELECT i.inventory_id, i.quantity, i.min_threshold, " +
                        "p.product_id, p.name, p.sku, p.price " +
                        "FROM inventory i JOIN product p ON i.product_id = p.product_id " +
                        "WHERE i.inventory_id = ?",
                inventoryMapper,
                id
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Inventory> findAll() {
        return jdbcTemplate.query(
                "SELECT i.inventory_id, i.quantity, i.min_threshold, " +
                        "p.product_id, p.name, p.sku, p.price " +
                        "FROM inventory i JOIN product p ON i.product_id = p.product_id",
                inventoryMapper
        );
    }

    @Override
    public void delete(Inventory inventory) {
        jdbcTemplate.update("DELETE FROM inventory WHERE inventory_id = ?", inventory.id());
    }
}