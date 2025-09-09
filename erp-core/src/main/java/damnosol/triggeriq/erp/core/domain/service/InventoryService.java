package damnosol.triggeriq.erp.core.domain.service;

import damnosol.triggeriq.erp.core.domain.model.Inventory;
import damnosol.triggeriq.erp.core.domain.repository.InventoryRepository;

import java.util.List;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void updateStock(Long inventoryId, int quantityChange) {
        Inventory inv = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        Inventory updated = inv.withQuantity(inv.quantity() + quantityChange);
        inventoryRepository.save(updated);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(Inventory::needsReorder)
                .toList();
    }
}