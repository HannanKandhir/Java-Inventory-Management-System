package inventory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {

    private static final String LOGIN_FILE = "loginHistory.txt";
    private static final String ACTIVITY_FILE = "activityLog.txt";
    private static final String PURCHASE_FILE = "purchaseHistory.txt";

    private static String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public static void logLogin(String username, String role) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(LOGIN_FILE, true));
            writer.println(getTime() + " | " + username + " logged in as " + role);
            writer.close();
        } catch (IOException e) {
            System.out.println("Login history saving error.");
        }
    }

    public static void logActivity(String message) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ACTIVITY_FILE, true));
            writer.println(getTime() + " | " + message);
            writer.close();
        } catch (IOException e) {
            System.out.println("Activity log saving error.");
        }
    }

    public static void logPurchase(String username, String itemName, int quantity, double totalPrice) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(PURCHASE_FILE, true));
            writer.println(getTime() + " | " + username + " bought " + itemName +
                    " | Quantity: " + quantity +
                    " | Total: " + totalPrice);
            writer.close();
        } catch (IOException e) {
            System.out.println("Purchase history saving error.");
        }
    }
}