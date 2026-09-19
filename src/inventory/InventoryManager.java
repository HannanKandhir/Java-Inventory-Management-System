package inventory;

import java.io.*;
import java.util.ArrayList;

public class InventoryManager {

    private ArrayList<Item> items = new ArrayList<>();
    private final String FILE_NAME = "inventory.txt";

    public void addItem(Item item) throws InvalidItemException {

        if (searchItem(item.getId()) != null) {
            throw new InvalidItemException("Item ID already exists.");
        }

        items.add(item);
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public Item searchItem(String id) {

        for (Item item : items) {

            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }

        return null;
    }

    // SEARCH BY PRODUCT NAME
    public Item searchItemByName(String name) {

        for (Item item : items) {

            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }

        return null;
    }

    // SEARCH BY ID OR NAME
    public Item searchByIdOrName(String id, String name) {

        if (id != null && !id.trim().isEmpty()) {
            return searchItem(id);
        }

        if (name != null && !name.trim().isEmpty()) {
            return searchItemByName(name);
        }

        return null;
    }

    public boolean deleteItem(String id) {

        Item item = searchItem(id);

        if (item != null) {
            items.remove(item);
            return true;
        }

        return false;
    }

    public boolean updateItem(String id, Item newItem) {

        for (int i = 0; i < items.size(); i++) {

            if (items.get(i).getId().equalsIgnoreCase(id)) {

                items.set(i, newItem);
                return true;
            }
        }

        return false;
    }

    public void sellItem(String id, int amount) throws InvalidItemException {

        Item item = searchItem(id);

        if (item == null) {
            throw new InvalidItemException("Item not found.");
        }

        item.sellItem(amount);
    }

    public void restockItem(String id, int amount) throws InvalidItemException {

        Item item = searchItem(id);

        if (item == null) {
            throw new InvalidItemException("Item not found.");
        }

        item.restockItem(amount);
    }

    public ArrayList<Item> getLowStockItems() {

        ArrayList<Item> lowStockItems = new ArrayList<>();

        for (Item item : items) {

            if (item.isLowStock()) {
                lowStockItems.add(item);
            }
        }

        return lowStockItems;
    }

    public int getOutOfStockCount() {

        int count = 0;

        for (Item item : items) {

            if (item.isOutOfStock()) {
                count++;
            }
        }

        return count;
    }

    public double getTotalInventoryValue() {

        double total = 0;

        for (Item item : items) {
            total += item.getTotalValue();
        }

        return total;
    }

    public void saveToFile() {

        try {

            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));

            for (Item item : items) {
                writer.println(item.toFileString());
            }

            writer.close();

        } catch (IOException e) {

            System.out.println("Error saving file.");
        }
    }

    public void loadFromFile() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try {

            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 7) {
                    continue;
                }

                String category = data[0];
                String id = data[1];
                String name = data[2];
                int quantity = Integer.parseInt(data[3]);
                double price = Double.parseDouble(data[4]);
                int lowStockLimit = Integer.parseInt(data[5]);
                String extra = data[6];

                Item item;

                if (category.equalsIgnoreCase("Food")) {

                    item = new FoodItem(
                            id,
                            name,
                            category,
                            quantity,
                            price,
                            lowStockLimit,
                            extra
                    );

                } else if (category.equalsIgnoreCase("Electronic")) {

                    item = new ElectronicItem(
                            id,
                            name,
                            category,
                            quantity,
                            price,
                            lowStockLimit,
                            extra
                    );

                } else {

                    item = new ClothingItem(
                            id,
                            name,
                            category,
                            quantity,
                            price,
                            lowStockLimit,
                            extra
                    );
                }

                items.add(item);
            }

            reader.close();

        } catch (Exception e) {

            System.out.println("Error loading file.");
        }
    }
}