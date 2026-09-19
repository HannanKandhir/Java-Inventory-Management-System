package inventory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryGUI extends Application {

    private InventoryManager manager = new InventoryManager();

    private ObservableList<Item> tableData = FXCollections.observableArrayList();
    private TableView<Item> tableView = new TableView<>();

    private TextField idField = new TextField();
    private TextField nameField = new TextField();
    private TextField quantityField = new TextField();
    private TextField priceField = new TextField();
    private TextField lowStockField = new TextField();
    private TextField extraField = new TextField();
    private TextField actionQuantityField = new TextField();
    private TextField searchField = new TextField();

    private TextField tableSearchField = new TextField();
    private ComboBox<String> categoryFilterBox = new ComboBox<>();
    private ComboBox<String> categoryBox = new ComboBox<>();

    private Label totalItemsLabel = new Label("0");
    private Label lowStockLabel = new Label("0");
    private Label outOfStockLabel = new Label("0");
    private Label valueLabel = new Label("0.00");
    private Label loggedInLabel = new Label();
    private Label lowStockAlertLabel = new Label();
    private Label stockHealthLabel = new Label();

    private BorderPane root;
    private Button themeButton;
    private boolean darkMode = false;
    private Stage mainStage;

    @Override
    public void start(Stage stage) {

        mainStage = stage;
        manager.loadFromFile();

        root = new BorderPane();
        root.getStyleClass().addAll("app-root", "light-theme");

        VBox topArea = new VBox(createHeader(), createStatusBar());

        root.setTop(topArea);
        root.setCenter(createMainContent(stage));
        root.setBottom(createFooter());

        refreshTable();

        Scene scene = new Scene(root, 1400, 780);

        try {
            scene.getStylesheets().add(getClass().getResource("/inventory/application.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found. Make sure application.css is inside src/inventory folder.");
        }

        stage.setTitle("Smart Inventory Management System");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox createHeader() {
        Label logo = new Label("SI");
        logo.getStyleClass().add("header-logo");

        Label title = new Label("Smart Inventory Management System");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("OOP Project | JavaFX Inventory Management System");
        subtitle.getStyleClass().add("app-subtitle");

        Label developers = new Label("Developed by: Abdul Hannan Kandhir, Tariq Jamali, Muhammad Qasim");
        developers.getStyleClass().add("app-small-text");

        Label roleBadge = new Label(Session.isAdmin() ? "ADMIN" : "USER");
        roleBadge.getStyleClass().add("role-badge");

        if (Session.isAdmin()) {
            loggedInLabel.setText("Logged in as: Abdul Hannan");
        } else {
            loggedInLabel.setText("Logged in as: " + Session.currentUsername);
        }

        loggedInLabel.getStyleClass().add("login-info");

        HBox loginRow = new HBox(10, loggedInLabel, roleBadge);
        loginRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3, title, subtitle, developers, loginRow);

        HBox leftBox = new HBox(15, logo, titleBox);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        themeButton = new Button("Dark Mode");
        themeButton.getStyleClass().add("theme-button");
        themeButton.setOnAction(e -> toggleTheme());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerRow = new HBox(20, leftBox, spacer, themeButton);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(headerRow);
        header.setPadding(new Insets(13, 28, 13, 28));
        header.getStyleClass().add("hero-header");

        return header;
    }

    private HBox createStatusBar() {
        String role = Session.isAdmin() ? "Admin" : "User";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy | hh:mm a"));

        Label systemStatus = new Label("System Status: Online");
        Label roleLabel = new Label("Role: " + role);
        Label dateLabel = new Label("Last Updated: " + dateTime);

        systemStatus.getStyleClass().add("status-bar-label");
        roleLabel.getStyleClass().add("status-bar-label");
        dateLabel.getStyleClass().add("status-bar-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBar = new HBox(18, systemStatus, roleLabel, spacer, dateLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(8, 30, 8, 30));
        statusBar.getStyleClass().add("top-status-bar");

        return statusBar;
    }

    private HBox createMainContent(Stage stage) {
        VBox sidebar = createSidebar(stage);
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        HBox main = new HBox(16, sidebar, leftPanel, rightPanel);
        main.setPadding(new Insets(16, 24, 10, 24));
        main.setAlignment(Pos.TOP_LEFT);

        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        return main;
    }

    private VBox createSidebar(Stage stage) {
        Label menuTitle = new Label("Inventory Menu");
        menuTitle.getStyleClass().add("sidebar-title");

        Button dashboardButton = makeSidebarButton("▣  Dashboard");
        Button showButton = makeSidebarButton("▤  Show All");
        Button sellButton = makeSidebarButton(Session.isUser() ? "$  Buy Item" : "$  Sell Item");
        Button restockButton = makeSidebarButton("↻  Restock");
        Button usersButton = makeSidebarButton("◎  Users");
        Button logsButton = makeSidebarButton("☰  Logs");
        Button historyButton = makeSidebarButton("◷  History");
        Button logoutButton = makeSidebarButton("×  Logout");

        dashboardButton.getStyleClass().add("sidebar-button-active");
        logoutButton.getStyleClass().add("sidebar-danger");

        dashboardButton.setOnAction(e -> {
            resetTableFilters();
            refreshTable();
        });

        showButton.setOnAction(e -> {
            resetTableFilters();
            refreshTable();
        });

        sellButton.setOnAction(e -> buyOrSellItem());
        restockButton.setOnAction(e -> restockItem());
        usersButton.setOnAction(e -> viewUsers());
        logsButton.setOnAction(e -> viewLogs());
        historyButton.setOnAction(e -> viewPurchaseHistory());
        logoutButton.setOnAction(e -> logout(stage));

        VBox sidebar = new VBox(11);
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(180);
        sidebar.setMinWidth(180);
        sidebar.setMaxWidth(180);
        sidebar.getStyleClass().add("sidebar");

        if (Session.isAdmin()) {
            sidebar.getChildren().addAll(
                    menuTitle,
                    dashboardButton,
                    showButton,
                    sellButton,
                    restockButton,
                    usersButton,
                    logsButton,
                    historyButton,
                    logoutButton
            );
        } else {
            sidebar.getChildren().addAll(
                    menuTitle,
                    dashboardButton,
                    showButton,
                    sellButton,
                    historyButton,
                    logoutButton
            );
        }

        return sidebar;
    }

    private Button makeSidebarButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(38);
        button.setMinHeight(38);
        button.getStyleClass().add("sidebar-button");
        return button;
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

    private VBox createLeftPanel() {
        Label formTitle = new Label("Product Details");
        formTitle.getStyleClass().add("section-title");

        Label formHint = new Label("Add, update, sell, restock, and search inventory items.");
        formHint.getStyleClass().add("section-hint");

        idField.setPromptText("Item ID");
        nameField.setPromptText("Item Name");
        quantityField.setPromptText("Stock Quantity");
        priceField.setPromptText("Price");
        lowStockField.setPromptText("Low Stock Limit");
        extraField.setPromptText("Expiry / Warranty / Size");
        actionQuantityField.setPromptText("Quantity to Buy / Restock");
        searchField.setPromptText("Search by ID or Name");

        categoryBox.getItems().addAll("Food", "Electronic", "Clothing");
        categoryBox.setValue("Food");

        addInputStyle(idField);
        addInputStyle(nameField);
        addInputStyle(quantityField);
        addInputStyle(priceField);
        addInputStyle(lowStockField);
        addInputStyle(extraField);
        addInputStyle(actionQuantityField);
        addInputStyle(searchField);
        categoryBox.getStyleClass().add("input-field");

        GridPane form = createForm();

        Label actionsTitle = new Label("Actions");
        actionsTitle.getStyleClass().add("actions-title");

        Separator divider = new Separator();
        divider.getStyleClass().add("form-divider");

        Button addButton = makeButton("Add", "add-button");
        Button updateButton = makeButton("Update", "update-button");
        Button deleteButton = makeButton("Delete", "delete-button");
        Button searchButton = makeButton("Search", "search-button");
        Button clearButton = makeButton("Clear", "clear-button");

        addButton.setOnAction(e -> addItem());
        updateButton.setOnAction(e -> updateItem());
        deleteButton.setOnAction(e -> deleteItem());
        searchButton.setOnAction(e -> searchProduct());
        clearButton.setOnAction(e -> clearFields());

        GridPane buttons = new GridPane();
        buttons.setHgap(8);
        buttons.setVgap(8);

        if (Session.isAdmin()) {
            buttons.add(addButton, 0, 0);
            buttons.add(updateButton, 1, 0);
            buttons.add(deleteButton, 2, 0);
            buttons.add(searchButton, 0, 1);
            buttons.add(clearButton, 1, 1);
        } else {
            buttons.add(searchButton, 0, 0);
            buttons.add(clearButton, 1, 0);

            categoryBox.setDisable(true);
            quantityField.setEditable(false);
            priceField.setEditable(false);
            lowStockField.setEditable(false);
            extraField.setEditable(false);
        }

        VBox titleBox = new VBox(2, formTitle, formHint);

        VBox card = new VBox(10, titleBox, form, divider, actionsTitle, buttons);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("content-card");

        ScrollPane scrollPane = new ScrollPane(card);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("clean-scroll");

        VBox leftPanel = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        leftPanel.setPrefWidth(430);
        leftPanel.setMinWidth(430);
        leftPanel.setMaxWidth(430);

        return leftPanel;
    }

    private VBox createRightPanel() {
        setupTable();

        Label tableTitle = new Label("Dashboard / Product Table");
        tableTitle.getStyleClass().add("section-title");

        Label tableHint = new Label("Formal inventory table with stock status and product value.");
        tableHint.getStyleClass().add("section-hint");

        Label welcomeLabel = new Label("Welcome back, " + (Session.isAdmin() ? "Abdul Hannan" : Session.currentUsername));
        welcomeLabel.getStyleClass().add("welcome-pill");

        VBox titleText = new VBox(2, tableTitle, tableHint);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(12, titleText, spacer, welcomeLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        HBox statsBox = createStatsCards();
        HBox toolbar = createTableToolbar();

        lowStockAlertLabel.getStyleClass().add("low-alert-card");

        VBox tableCard = new VBox(8, topRow, statsBox, stockHealthLabel, lowStockAlertLabel, toolbar, tableView);
        tableCard.setPadding(new Insets(16));
        tableCard.getStyleClass().add("content-card");
        tableCard.setMaxWidth(Double.MAX_VALUE);

        VBox rightPanel = new VBox(tableCard);
        rightPanel.setMinWidth(0);
        rightPanel.setMaxWidth(Double.MAX_VALUE);

        return rightPanel;
    }

    private HBox createTableToolbar() {
        tableSearchField.setPromptText("Search by ID, name, category, or status");
        tableSearchField.getStyleClass().add("table-search-field");

        categoryFilterBox.getItems().clear();
        categoryFilterBox.getItems().addAll("All Categories", "Food", "Electronic", "Clothing");
        categoryFilterBox.setValue("All Categories");
        categoryFilterBox.getStyleClass().add("table-filter-box");

        Button filterButton = new Button("Filter");
        filterButton.getStyleClass().addAll("small-action-button", "search-button");

        Button resetButton = new Button("Reset");
        resetButton.getStyleClass().addAll("small-action-button", "show-button");

        filterButton.setOnAction(e -> applyTableFilters());

        resetButton.setOnAction(e -> {
            resetTableFilters();
            refreshTable();
        });

        HBox toolbar = new HBox(8, tableSearchField, categoryFilterBox, filterButton, resetButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("table-toolbar");

        HBox.setHgrow(tableSearchField, Priority.ALWAYS);

        return toolbar;
    }

    private void resetTableFilters() {
        tableSearchField.clear();
        categoryFilterBox.setValue("All Categories");
    }

    private void applyTableFilters() {
        String keyword = tableSearchField.getText().trim().toLowerCase();
        String selectedCategory = categoryFilterBox.getValue();

        tableData.clear();

        for (Item item : manager.getAllItems()) {
            boolean keywordMatches =
                    keyword.isEmpty()
                            || item.getId().toLowerCase().contains(keyword)
                            || item.getName().toLowerCase().contains(keyword)
                            || item.getCategory().toLowerCase().contains(keyword)
                            || item.getStockStatus().toLowerCase().contains(keyword);

            boolean categoryMatches =
                    selectedCategory.equals("All Categories")
                            || item.getCategory().equalsIgnoreCase(selectedCategory);

            if (keywordMatches && categoryMatches) {
                tableData.add(item);
            }
        }

        updateStats();
    }

    private HBox createStatsCards() {
        VBox totalCard = createStatsCard("Total Items", totalItemsLabel, "blue-value", "total-card");
        VBox lowCard = createStatsCard("Low Stock", lowStockLabel, "orange-value", "low-card");
        VBox outCard = createStatsCard("Out of Stock", outOfStockLabel, "red-value", "out-card");
        VBox valueCard = createStatsCard("Inventory Value", valueLabel, "green-value", "value-card");

        HBox statsBox = new HBox(10, totalCard, lowCard, outCard, valueCard);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        totalCard.setMinWidth(110);
        lowCard.setMinWidth(110);
        outCard.setMinWidth(110);
        valueCard.setMinWidth(145);

        totalCard.setMaxWidth(Double.MAX_VALUE);
        lowCard.setMaxWidth(Double.MAX_VALUE);
        outCard.setMaxWidth(Double.MAX_VALUE);
        valueCard.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(lowCard, Priority.ALWAYS);
        HBox.setHgrow(outCard, Priority.ALWAYS);
        HBox.setHgrow(valueCard, Priority.ALWAYS);

        return statsBox;
    }

    private VBox createStatsCard(String title, Label value, String valueStyle, String cardStyle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboard-title");

        value.getStyleClass().addAll("dashboard-value", valueStyle);

        VBox card = new VBox(2, titleLabel, value);
        card.setPadding(new Insets(9));
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().addAll("dashboard-card", cardStyle);

        return card;
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(7);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(120);
        labelColumn.setPrefWidth(120);

        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHgrow(Priority.ALWAYS);

        form.getColumnConstraints().addAll(labelColumn, inputColumn);

        form.add(makeLabel("Search Product"), 0, 0);
        form.add(searchField, 1, 0);

        form.add(makeLabel("Item ID"), 0, 1);
        form.add(idField, 1, 1);

        form.add(makeLabel("Item Name"), 0, 2);
        form.add(nameField, 1, 2);

        form.add(makeLabel("Category"), 0, 3);
        form.add(categoryBox, 1, 3);

        form.add(makeLabel("Stock Quantity"), 0, 4);
        form.add(quantityField, 1, 4);

        form.add(makeLabel("Price"), 0, 5);
        form.add(priceField, 1, 5);

        form.add(makeLabel("Low Stock Limit"), 0, 6);
        form.add(lowStockField, 1, 6);

        form.add(makeLabel("Extra Info"), 0, 7);
        form.add(extraField, 1, 7);

        form.add(makeLabel("Quantity Action"), 0, 8);
        form.add(actionQuantityField, 1, 8);

        return form;
    }

    private void setupTable() {
        TableColumn<Item, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Item, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Item, Integer> limitCol = new TableColumn<>("Low Limit");
        limitCol.setCellValueFactory(new PropertyValueFactory<>("lowStockLimit"));

        TableColumn<Item, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("stockStatus"));

        statusCol.setCellFactory(column -> new TableCell<Item, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("status-badge");

                    if (status.equalsIgnoreCase("Available")) {
                        badge.getStyleClass().add("status-available");
                    } else if (status.equalsIgnoreCase("Low Stock")) {
                        badge.getStyleClass().add("status-low");
                    } else {
                        badge.getStyleClass().add("status-out");
                    }

                    setText(null);
                    setGraphic(badge);
                }
            }
        });

        tableView.getColumns().clear();
        tableView.getColumns().addAll(idCol, nameCol, categoryCol, quantityCol, priceCol, limitCol, statusCol);

        tableView.setItems(tableData);
        tableView.setPrefHeight(340);
        tableView.setMinHeight(340);
        tableView.setMaxHeight(340);
        tableView.setFixedCellSize(36);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().add("product-table");

        tableView.setRowFactory(table -> {
            TableRow<Item> row = new TableRow<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll("available-row", "low-stock-row", "out-stock-row");

                    if (!empty && item != null) {
                        if (item.isOutOfStock()) {
                            getStyleClass().add("out-stock-row");
                        } else if (item.isLowStock()) {
                            getStyleClass().add("low-stock-row");
                        } else {
                            getStyleClass().add("available-row");
                        }
                    }
                }
            };

            row.setOnMouseClicked(e -> {
                Item selected = row.getItem();

                if (selected != null) {
                    fillFields(selected);
                }
            });

            return row;
        });
    }

    private void fillFields(Item item) {
        idField.setText(item.getId());
        nameField.setText(item.getName());
        categoryBox.setValue(item.getCategory());
        quantityField.setText(String.valueOf(item.getQuantity()));
        priceField.setText(String.valueOf(item.getPrice()));
        lowStockField.setText(String.valueOf(item.getLowStockLimit()));

        if (item instanceof FoodItem) {
            extraField.setText(((FoodItem) item).getExpiryDate());
        } else if (item instanceof ElectronicItem) {
            extraField.setText(((ElectronicItem) item).getWarranty());
        } else if (item instanceof ClothingItem) {
            extraField.setText(((ClothingItem) item).getSize());
        }
    }

    private void addInputStyle(TextField field) {
        field.getStyleClass().add("input-field");
    }

    private Label makeLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private Button makeButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setMinWidth(88);
        button.setPrefWidth(88);
        button.setPrefHeight(32);
        button.getStyleClass().addAll("action-button", styleClass);
        return button;
    }

    private Item createItemFromInput() throws InvalidItemException {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String category = categoryBox.getValue();
        String extra = extraField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || extra.isEmpty()) {
            throw new InvalidItemException("Please fill all item detail fields.");
        }

        int quantity;
        double price;
        int lowStockLimit;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            price = Double.parseDouble(priceField.getText().trim());
            lowStockLimit = Integer.parseInt(lowStockField.getText().trim());
        } catch (NumberFormatException e) {
            throw new InvalidItemException("Quantity, price, and low stock limit must be numbers.");
        }

        if (quantity < 0 || price < 0 || lowStockLimit < 0) {
            throw new InvalidItemException("Quantity, price, and low stock limit cannot be negative.");
        }

        if (category.equalsIgnoreCase("Food")) {
            return new FoodItem(id, name, category, quantity, price, lowStockLimit, extra);
        } else if (category.equalsIgnoreCase("Electronic")) {
            return new ElectronicItem(id, name, category, quantity, price, lowStockLimit, extra);
        } else {
            return new ClothingItem(id, name, category, quantity, price, lowStockLimit, extra);
        }
    }

    private int getActionQuantity() throws InvalidItemException {
        try {
            int amount = Integer.parseInt(actionQuantityField.getText().trim());

            if (amount <= 0) {
                throw new InvalidItemException("Quantity must be greater than 0.");
            }

            return amount;

        } catch (NumberFormatException e) {
            throw new InvalidItemException("Enter a valid quantity.");
        }
    }

    private void addItem() {
        try {
            Item item = createItemFromInput();

            manager.addItem(item);
            manager.saveToFile();
            ActivityLogger.logActivity(Session.currentUsername + " added item: " + item.getName());

            refreshTable();

            showStyledBill(
                    "Item Added Successfully",
                    "Product has been added to inventory.",
                    new String[][]{
                            {"Product", item.getName()},
                            {"Category", item.getCategory()},
                            {"Quantity", String.valueOf(item.getQuantity())},
                            {"Price", String.valueOf(item.getPrice())},
                            {"Status", item.getStockStatus()}
                    },
                    "success"
            );

            clearFields();

        } catch (InvalidItemException e) {
            showError(e.getMessage());
        }
    }

    private void updateItem() {
        try {
            String id = idField.getText().trim();

            if (id.isEmpty()) {
                showError("Enter item ID to update.");
                return;
            }

            Item newItem = createItemFromInput();

            if (manager.updateItem(id, newItem)) {
                manager.saveToFile();
                ActivityLogger.logActivity(Session.currentUsername + " updated item: " + newItem.getName());

                refreshTable();

                showStyledBill(
                        "Item Updated Successfully",
                        "Product details have been updated.",
                        new String[][]{
                                {"Product", newItem.getName()},
                                {"Category", newItem.getCategory()},
                                {"Quantity", String.valueOf(newItem.getQuantity())},
                                {"Price", String.valueOf(newItem.getPrice())},
                                {"Status", newItem.getStockStatus()}
                        },
                        "success"
                );

                clearFields();
            } else {
                showError("Item not found.");
            }

        } catch (InvalidItemException e) {
            showError(e.getMessage());
        }
    }

    private void deleteItem() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showError("Enter item ID to delete.");
            return;
        }

        Item item = manager.searchItem(id);

        if (item == null) {
            showError("Item not found.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Item");
        confirm.setHeaderText("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete " + item.getName() + "?");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        manager.deleteItem(id);
        manager.saveToFile();
        ActivityLogger.logActivity(Session.currentUsername + " deleted item: " + item.getName());

        refreshTable();

        showStyledBill(
                "Item Deleted Successfully",
                "Product has been removed from inventory.",
                new String[][]{
                        {"Deleted Product", item.getName()},
                        {"Category", item.getCategory()},
                        {"Previous Quantity", String.valueOf(item.getQuantity())}
                },
                "danger"
        );

        clearFields();
    }

    private void searchProduct() {
        String text = searchField.getText().trim();

        if (text.isEmpty()) {
            text = idField.getText().trim();

            if (text.isEmpty()) {
                text = nameField.getText().trim();
            }
        }

        if (text.isEmpty()) {
            showError("Enter product ID or name to search.");
            return;
        }

        Item item = manager.searchByIdOrName(text, text);

        if (item == null) {
            showError("Product not found.");
            return;
        }

        tableData.clear();
        tableData.add(item);
        fillFields(item);
    }

    private void buyOrSellItem() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() && name.isEmpty()) {
                showError("Select or search a product first.");
                return;
            }

            int amount = getActionQuantity();

            Item itemBefore = manager.searchByIdOrName(id, name);

            if (itemBefore == null) {
                showError("Product not found.");
                return;
            }

            double totalPrice = amount * itemBefore.getPrice();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle(Session.isUser() ? "Confirm Purchase" : "Confirm Sale");
            confirm.setHeaderText("Product: " + itemBefore.getName());
            confirm.setContentText("Quantity: " + amount + "\nTotal Price: " + totalPrice);

            if (confirm.showAndWait().get() != ButtonType.OK) {
                return;
            }

            manager.sellItem(itemBefore.getId(), amount);
            manager.saveToFile();

            Item itemAfter = manager.searchItem(itemBefore.getId());

            if (Session.isUser()) {
                ActivityLogger.logPurchase(Session.currentUsername, itemAfter.getName(), amount, totalPrice);
                ActivityLogger.logActivity(Session.currentUsername + " bought " + itemAfter.getName()
                        + " | Quantity: " + amount + " | Total: " + totalPrice);

                showStyledBill(
                        "Purchase Receipt",
                        "Purchase completed successfully.",
                        new String[][]{
                                {"User", Session.currentUsername},
                                {"Product", itemAfter.getName()},
                                {"Quantity Bought", String.valueOf(amount)},
                                {"Price Per Item", String.valueOf(itemAfter.getPrice())},
                                {"Total Price", String.format("%.2f", totalPrice)},
                                {"Remaining Stock", String.valueOf(itemAfter.getQuantity())}
                        },
                        "success"
                );
            } else {
                ActivityLogger.logActivity(Session.currentUsername + " sold " + itemAfter.getName()
                        + " | Quantity: " + amount + " | Total: " + totalPrice);

                showStyledBill(
                        "Sale Receipt",
                        "Sale completed successfully.",
                        new String[][]{
                                {"Admin", Session.currentUsername},
                                {"Product", itemAfter.getName()},
                                {"Quantity Sold", String.valueOf(amount)},
                                {"Price Per Item", String.valueOf(itemAfter.getPrice())},
                                {"Total Price", String.format("%.2f", totalPrice)},
                                {"Remaining Stock", String.valueOf(itemAfter.getQuantity())}
                        },
                        "success"
                );
            }

            if (itemAfter.isOutOfStock()) {
                showWarning("Out of Stock", itemAfter.getName() + " is now out of stock.");
            } else if (itemAfter.isLowStock()) {
                showWarning("Low Stock Alert", itemAfter.getName() + " is now below the low stock limit.");
            }

            refreshTable();
            fillFields(itemAfter);
            actionQuantityField.clear();

        } catch (InvalidItemException e) {
            showError(e.getMessage());
        }
    }

    private void restockItem() {
        try {
            String id = idField.getText().trim();

            if (id.isEmpty()) {
                showError("Select or enter item ID to restock.");
                return;
            }

            int amount = getActionQuantity();

            manager.restockItem(id, amount);
            manager.saveToFile();

            Item item = manager.searchItem(id);

            ActivityLogger.logActivity(Session.currentUsername + " restocked " + item.getName()
                    + " | Quantity: " + amount);

            refreshTable();
            fillFields(item);

            showStyledBill(
                    "Restock Receipt",
                    "Product stock has been updated.",
                    new String[][]{
                            {"Product", item.getName()},
                            {"Quantity Added", String.valueOf(amount)},
                            {"New Stock", String.valueOf(item.getQuantity())},
                            {"Status", item.getStockStatus()}
                    },
                    "success"
            );

            actionQuantityField.clear();

        } catch (InvalidItemException e) {
            showError(e.getMessage());
        }
    }

    private void showStyledBill(String title, String subtitle, String[][] rows, String type) {
        Stage billStage = new Stage();
        billStage.setTitle(title);
        billStage.initModality(Modality.APPLICATION_MODAL);

        if (mainStage != null) {
            billStage.initOwner(mainStage);
        }

        Label icon = new Label(type.equals("danger") ? "!" : "✓");
        icon.getStyleClass().add(type.equals("danger") ? "receipt-icon-danger" : "receipt-icon-success");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("receipt-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("receipt-subtitle");

        VBox headerText = new VBox(4, titleLabel, subtitleLabel);
        headerText.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(12, icon, headerText);
        header.setAlignment(Pos.CENTER_LEFT);

        GridPane detailGrid = new GridPane();
        detailGrid.setHgap(20);
        detailGrid.setVgap(10);
        detailGrid.getStyleClass().add("receipt-grid");

        for (int i = 0; i < rows.length; i++) {
            Label key = new Label(rows[i][0]);
            key.getStyleClass().add("receipt-key");

            Label value = new Label(rows[i][1]);
            value.getStyleClass().add("receipt-value");

            detailGrid.add(key, 0, i);
            detailGrid.add(value, 1, i);
        }

        Label footer = new Label("Smart Inventory Management System");
        footer.getStyleClass().add("receipt-footer");

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("receipt-ok-button");
        okButton.setOnAction(e -> billStage.close());

        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(16, header, detailGrid, footer, buttonBox);
        card.setPadding(new Insets(22));
        card.getStyleClass().add("receipt-card");

        StackPane rootPane = new StackPane(card);
        rootPane.setPadding(new Insets(18));
        rootPane.getStyleClass().add("receipt-window-root");

        Scene scene = new Scene(rootPane, 430, 470);

        try {
            scene.getStylesheets().add(getClass().getResource("/inventory/application.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS not loaded for receipt.");
        }

        billStage.setScene(scene);
        billStage.setResizable(false);
        billStage.showAndWait();
    }

    private void viewUsers() {
        StringBuilder text = new StringBuilder("Registered Users:\n\n");

        File file = new File("users.txt");

        if (!file.exists()) {
            showInfo("No users found.");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 3) {
                    text.append("Username: ").append(data[0])
                            .append(" | Role: ").append(data[2]).append("\n");
                }
            }

            reader.close();
            showLargeText("Registered Users", text.toString());

        } catch (Exception e) {
            showError("Error reading users file.");
        }
    }

    private void viewLogs() {
        String text =
                "Login History:\n" + readFile("loginHistory.txt") +
                        "\n\nActivity Log:\n" + readFile("activityLog.txt") +
                        "\n\nPurchase History:\n" + readFile("purchaseHistory.txt");

        showLargeText("System Logs", text);
    }

    private void viewPurchaseHistory() {
        File file = new File("purchaseHistory.txt");

        if (!file.exists()) {
            showInfo("No purchase history found.");
            return;
        }

        StringBuilder text = new StringBuilder("Purchase History:\n\n");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                if (Session.isAdmin() || line.contains(Session.currentUsername)) {
                    text.append(line).append("\n");
                }
            }

            reader.close();
            showLargeText("Purchase History", text.toString());

        } catch (Exception e) {
            showError("Error reading purchase history.");
        }
    }

    private String readFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            return "No records found.\n";
        }

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }

            reader.close();

        } catch (Exception e) {
            return "Error reading file.\n";
        }

        return text.toString();
    }

    private void logout(Stage stage) {
        ActivityLogger.logActivity(Session.currentUsername + " logged out.");
        Session.clearSession();

        stage.close();

        try {
            LoginGUI loginGUI = new LoginGUI();
            Stage loginStage = new Stage();
            loginGUI.start(loginStage);
        } catch (Exception e) {
            System.out.println("Logout error.");
        }
    }

    private void refreshTable() {
        tableData.clear();
        tableData.addAll(manager.getAllItems());
        updateStats();
    }

    private void updateStats() {
        totalItemsLabel.setText(String.valueOf(manager.getAllItems().size()));
        lowStockLabel.setText(String.valueOf(manager.getLowStockItems().size()));
        outOfStockLabel.setText(String.valueOf(manager.getOutOfStockCount()));
        valueLabel.setText(String.format("%.2f", manager.getTotalInventoryValue()));

        int lowCount = manager.getLowStockItems().size();
        int outCount = manager.getOutOfStockCount();

        if (outCount > 0 || lowCount > 0) {
            stockHealthLabel.setText("Stock Health: Needs Attention");
            stockHealthLabel.getStyleClass().removeAll("stock-health-good", "stock-health-warning");
            stockHealthLabel.getStyleClass().add("stock-health-warning");

            lowStockAlertLabel.setText("Attention: " + lowCount + " low-stock item(s), "
                    + outCount + " out-of-stock item(s). Restock soon.");
        } else {
            stockHealthLabel.setText("Stock Health: Excellent");
            stockHealthLabel.getStyleClass().removeAll("stock-health-good", "stock-health-warning");
            stockHealthLabel.getStyleClass().add("stock-health-good");

            lowStockAlertLabel.setText("Inventory looks healthy. No low-stock items right now.");
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        quantityField.clear();
        priceField.clear();
        lowStockField.clear();
        extraField.clear();
        actionQuantityField.clear();
        searchField.clear();
        tableSearchField.clear();
        categoryFilterBox.setValue("All Categories");
        categoryBox.setValue("Food");
        refreshTable();
    }

    private HBox createFooter() {
        Label footerText = new Label("Smart Inventory Management System | OOP Project | JavaFX");
        footerText.getStyleClass().add("footer-text");

        HBox footer = new HBox(footerText);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(6, 20, 8, 20));
        footer.getStyleClass().add("footer-bar");

        return footer;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLargeText(String title, String text) {
        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(650);
        area.setPrefHeight(450);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}