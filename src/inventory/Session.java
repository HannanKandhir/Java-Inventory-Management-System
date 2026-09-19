package inventory;

public class Session {

    public static String currentUsername = "";
    public static String currentRole = "";

    public static boolean isAdmin() {
        return currentRole.equalsIgnoreCase("ADMIN");
    }

    public static boolean isUser() {
        return currentRole.equalsIgnoreCase("USER");
    }

    public static void clearSession() {
        currentUsername = "";
        currentRole = "";
    }
}