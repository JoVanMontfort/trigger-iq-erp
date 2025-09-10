package damnosol.triggeriq.erp.infrastructure.persistence;

import damnosol.triggeriq.erp.core.domain.model.Inventory;
import damnosol.triggeriq.erp.infra.persistence.JdbcInventoryRepository;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcInventoryRepositoryIT {

    private static final Logger log = LoggerFactory.getLogger(JdbcInventoryRepositoryIT.class);

    @Container
    public static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("triggerIq")
                    .withUsername("triggerIq")
                    .withPassword("triggerIq");

    private JdbcInventoryRepository repository;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() throws Exception {
        log.info("Setting up DataSource and JdbcTemplate");
        DataSource dataSource = new DriverManagerDataSource(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        repository = new JdbcInventoryRepository(jdbcTemplate);

        // Initialize DB using Liquibase master changelog
        try (Connection connection = dataSource.getConnection()) {
            log.info("Initializing database schema via Liquibase master changelog");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update("");
        }
    }

    @Test
    void testSaveAndFindInventory() {
        // Insert product first (FK requirement)
        log.info("Inserting product for FK");
        int productRows = jdbcTemplate.update(
                "INSERT INTO product(id,name,sku,price) VALUES (?,?,?,?)",
                1L, "Test Product", "SKU001", 19.99
        );
        log.info("Rows inserted into product: {}", productRows);

        Inventory inventory = new Inventory(null, 1L, 100, 10);
        Inventory savedInventory = repository.save(inventory);
        log.info("Saved inventory: {}", savedInventory);

        Inventory fetched = repository.findById(savedInventory.id()).orElse(null);
        assertNotNull(fetched, "Inventory should not be null after save");
        assertEquals(100, fetched.quantity(), "Quantity should match");
        assertEquals(1L, fetched.product(), "Product ID should match");

        // Update
        Inventory updatedInventory = fetched.withQuantity(50);
        repository.update(updatedInventory);
        Inventory afterUpdate = repository.findById(fetched.id()).orElse(null);
        assertNotNull(afterUpdate);
        assertEquals(50, afterUpdate.quantity());

        // Delete
        repository.delete(fetched.id());
        Inventory afterDelete = repository.findById(fetched.id()).orElse(null);
        assertNull(afterDelete, "Inventory should be null after delete");
    }
}