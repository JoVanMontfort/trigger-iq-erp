package damnosol.triggeriq.erp.core.domain.repository;

import damnosol.triggeriq.erp.core.domain.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    /**
     * Save or update an inventory record
     *
     * @param inventory the inventory to save
     * @return the saved inventory
     */
    Inventory save(Inventory inventory);

    /**
     * Find an inventory record by its ID
     *
     * @param id the inventory ID
     * @return Optional of Inventory
     */
    Optional<Inventory> findById(Long id);

    /**
     * Get all inventory records
     *
     * @return List of Inventory
     */
    List<Inventory> findAll();

    /**
     * Delete an inventory record
     *
     * @param inventory the inventory to delete
     */
    void delete(Inventory inventory);
}