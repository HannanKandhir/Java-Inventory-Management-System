package inventory;

public class ElectronicItem extends Item {

    private String warranty;

    public ElectronicItem(String id, String name, String category, int quantity, double price, int lowStockLimit, String warranty) {
        super(id, name, category, quantity, price, lowStockLimit);
        this.warranty = warranty;
    }

    public String getWarranty() {
        return warranty;
    }

    @Override
    public String displayDetails() {
        return super.displayDetails() + " | Warranty: " + warranty;
    }

    @Override
    public String toFileString() {
        return getCategory() + "," + getId() + "," + getName() + "," +
                getQuantity() + "," + getPrice() + "," + getLowStockLimit() + "," + warranty;
    }
}