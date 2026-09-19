package inventory;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginGUI extends Application {

    private UserManager userManager = new UserManager();

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();

    private BorderPane root;
    private Button themeButton;
    private boolean darkMode = false;

    @Override
    public void start(Stage stage) {

        root = new BorderPane();
        root.getStyleClass().addAll("login-root", "light-theme");

        VBox loginCard = createLoginCard(stage);

        themeButton = new Button("Dark Mode");
        themeButton.getStyleClass().add("login-theme-button");
        themeButton.setOnAction(e -> toggleTheme());

        HBox topBar = new HBox(themeButton);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(18, 24, 0, 24));

        StackPane centerPane = new StackPane(loginCard);
        centerPane.setPadding(new Insets(18, 24, 26, 24));

        root.setTop(topBar);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 540, 520);

        try {
            scene.getStylesheets().add(getClass().getResource("/inventory/application.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found. Make sure application.css is inside src/inventory folder.");
        }

        stage.setTitle("Smart Inventory Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private VBox createLoginCard(Stage stage) {

        Label logo = new Label("SI");
        logo.getStyleClass().add("login-logo-box");

        Label title = new Label("Smart Inventory Login");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Secure access to your inventory dashboard");
        subtitle.getStyleClass().add("login-subtitle");

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        usernameField.getStyleClass().add("login-input");
        passwordField.getStyleClass().add("login-input");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-main-button");

        Button signupButton = new Button("Create Account");
        signupButton.getStyleClass().add("login-secondary-button");

        loginButton.setOnAction(e -> login(stage));
        signupButton.setOnAction(e -> signup());

        Label hintLabel = new Label("Enter your username and password to continue.");
        hintLabel.getStyleClass().add("login-hint");

        VBox card = new VBox(13);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30, 36, 30, 36));
        card.setPrefWidth(420);
        card.setMaxWidth(420);
        card.getStyleClass().add("login-card");

        card.getChildren().addAll(
                logo,
                title,
                subtitle,
                usernameField,
                passwordField,
                loginButton,
                signupButton,
                hintLabel
        );

        return card;
    }

    private void toggleTheme() {
        darkMode = !darkMode;

        if (darkMode) {
            root.getStyleClass().remove("light-theme");
            root.getStyleClass().add("dark-theme");
            themeButton.setText("Light Mode");
        } else {
            root.getStyleClass().remove("dark-theme");
            root.getStyleClass().add("light-theme");
            themeButton.setText("Dark Mode");
        }
    }

    private void login(Stage loginStage) {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            userManager.login(username, password);

            InventoryGUI inventoryGUI = new InventoryGUI();
            Stage inventoryStage = new Stage();
            inventoryGUI.start(inventoryStage);

            loginStage.close();

        } catch (InvalidItemException e) {
            showAlert(e.getMessage());
        }
    }

    private void signup() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            userManager.signup(username, password);

            showInfo("Account created successfully. You can now login.");

            usernameField.clear();
            passwordField.clear();

        } catch (InvalidItemException e) {
            showAlert(e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}