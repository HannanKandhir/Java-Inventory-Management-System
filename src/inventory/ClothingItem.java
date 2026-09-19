package inventory;

public class ClothingItem extends Item {

    private String size;

    public ClothingItem(String id, String name, String category, int quantity, double price, int lowStockLimit, String size) {
        super(id, name, category, quantity, price, lowStockLimit);
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String displayDetails() {
        return super.displayDetails() + " | Size: " + size;
    }

    @Override
    public String toFileString() {
        return getCategory() + "," + getId() + "," + getName() + "," +
                getQuantity() + "," + getPrice() + "," + getLowStockLimit() + "," + size;
    }
}