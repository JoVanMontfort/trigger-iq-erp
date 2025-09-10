package damnosol.triggeriq.erp.core.domain.model;


public record Inventory(
        Long id,
        long product,
        int quantity,
        int minThreshold
) {
    public boolean needsReorder() {
        return quantity <= minThreshold;
    }

    public Inventory withQuantity(int newQuantity) {
        return new Inventory(id, product, newQuantity, minThreshold);
    }
}