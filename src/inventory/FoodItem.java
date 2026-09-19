package inventory;

public class FoodItem extends Item {

    private String expiryDate;

    public FoodItem(String id, String name, String category, int quantity, double price, int lowStockLimit, String expiryDate) {
        super(id, name, category, quantity, price, lowStockLimit);
        this.expiryDate = expiryDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String displayDetails() {
        return super.displayDetails() + " | Expiry: " + expiryDate;
    }

    @Override
    public String toFileString() {
        return getCategory() + "," + getId() + "," + getName() + "," +
                getQuantity() + "," + getPrice() + "," + getLowStockLimit() + "," + expiryDate;
    }
}