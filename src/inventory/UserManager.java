package inventory;

import java.io.*;
import java.util.ArrayList;

public class UserManager {

    private ArrayList<User> users = new ArrayList<>();
    private final String FILE_NAME = "users.txt";

    public UserManager() {
        loadUsers();

        if (users.isEmpty()) {
            users.add(new User("admin", "admin123", "ADMIN"));
            saveUsers();
        }
    }

    public void signup(String username, String password) throws InvalidItemException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new InvalidItemException("Username and password cannot be empty.");
        }

        if (findUser(username) != null) {
            throw new InvalidItemException("Username already exists.");
        }

        users.add(new User(username, password, "USER"));
        saveUsers();

        ActivityLogger.logActivity("New user signed up: " + username);
    }

    public User login(String username, String password) throws InvalidItemException {
        User user = findUser(username);

        if (user == null) {
            throw new InvalidItemException("User not found.");
        }

        if (!user.getPassword().equals(password)) {
            throw new InvalidItemException("Incorrect password.");
        }

        Session.currentUsername = user.getUsername();
        Session.currentRole = user.getRole();

        ActivityLogger.logLogin(user.getUsername(), user.getRole());

        return user;
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public void saveUsers() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));

            for (User user : users) {
                writer.println(user.toFileString());
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("User saving error.");
        }
    }

    public void loadUsers() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 3) {
                    continue;
                }

                users.add(new User(data[0], data[1], data[2]));
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("User loading error.");
        }
    }
}