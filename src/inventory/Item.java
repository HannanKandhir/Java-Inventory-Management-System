package inventory;

public class Item {

    private String id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private int lowStockLimit;

    public Item(String id, String name, String category, int quantity, double price, int lowStockLimit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.lowStockLimit = lowStockLimit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getLowStockLimit() {
        return lowStockLimit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isLowStock() {
        return quantity > 0 && quantity <= lowStockLimit;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    public void sellItem(int amount) throws InvalidItemException {
        if (amount <= 0) {
            throw new InvalidItemException("Sell quantity must be greater than 0.");
        }

        if (amount > quantity) {
            throw new InvalidItemException("Not enough stock available.");
        }

        quantity -= amount;
    }

    public void restockItem(int amount) throws InvalidItemException {
        if (amount <= 0) {
            throw new InvalidItemException("Restock quantity must be greater than 0.");
        }

        quantity += amount;
    }

    public double getTotalValue() {
        return quantity * price;
    }

    public String getStockStatus() {
        if (isOutOfStock()) {
            return "Out of Stock";
        } else if (isLowStock()) {
            return "Low Stock";
        } else {
            return "Available";
        }
    }

    public String displayDetails() {
        return "ID: " + id +
                " | Name: " + name +
                " | Category: " + category +
                " | Quantity: " + quantity +
                " | Price: " + price +
                " | Low Limit: " + lowStockLimit +
                " | Status: " + getStockStatus();
    }

    public String toFileString() {
        return category + "," + id + "," + name + "," + quantity + "," + price + "," + lowStockLimit + ",None";
    }
}