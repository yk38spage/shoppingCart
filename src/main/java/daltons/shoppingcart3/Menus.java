package daltons.shoppingcart3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Menus implements CanQuery {
    private Scanner input;
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Cart cart = new Cart();
    private User loggedInUser;

    public Menus() {
        initializeProducts();
        initializeUsers();
        this.input = new Scanner(System.in);
    }


    @Override
    public String getDBWhereQuery(String tableName) {
        return String.format("SELECT * FROM %s WHERE user_id = ?", tableName);
    }

    @Override
    public String getDB(String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }

    @Override
    public void initializeUsers() {
        // Some SQL query
        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = getDB("users");
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String email = resultSet.getString("email");
                String password = resultSet.getString("secret_code");
                User user = new User(name, surname, email, password);
                users.add(user);
                
                String addressSql = getDBWhereQuery("addresses");
                PreparedStatement addressStatement = connection.prepareStatement(addressSql);
                addressStatement.setInt(1, resultSet.getInt("id"));
                addressStatement.execute();
                ResultSet addressResultSet = addressStatement.getResultSet();
                while (addressResultSet.next()) {
                    String address = addressResultSet.getString("address");
                    user.addAddress(address);
                }

                String cardSql = getDBWhereQuery("cards");
                PreparedStatement cardStatement = connection.prepareStatement(cardSql);
                cardStatement.setInt(1, resultSet.getInt("id"));
                cardStatement.execute();
                ResultSet cardResultSet = cardStatement.getResultSet();
                while (cardResultSet.next()) {
                    String cardNumber = cardResultSet.getString("card_number");
                    String cvv = cardResultSet.getString("cvv");
                    String expiryDate = cardResultSet.getString("expiry_date");
                    CardDetail cardDetail = new CardDetail(cardNumber, cvv, expiryDate);
                    user.addCardDetail(cardDetail);
                }

                
                String orderHistorySql = getDBWhereQuery("shop_history");
                PreparedStatement orderHistoryStatement = connection.prepareStatement(orderHistorySql);
                orderHistoryStatement.setInt(1, resultSet.getInt("id"));
                orderHistoryStatement.execute();
                ResultSet orderHistoryResultSet = orderHistoryStatement.getResultSet();
                while (orderHistoryResultSet.next()) {
                    int orderId = orderHistoryResultSet.getInt("id");
                    JSONArray orderProducts = new JSONArray(orderHistoryResultSet.getString("products"));
                    List<Product> orderHistoryProducts = new ArrayList<>();
                    for (int i = 0; i < orderProducts.length(); i++) {
                        int productId = orderProducts.getInt(i);
                        for (Product product : products) {
                            if (product.getId() == productId) {
                                orderHistoryProducts.add(product);
                                break;
                            }
                        }
                    }
                    
                    String status = orderHistoryResultSet.getString("status");
                    Order order = new Order(orderId, orderHistoryProducts, status);
                    user.addOrderHistory(order);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initializeProducts() {
        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = getDB("products");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
                try (ResultSet resultSet = statement.getResultSet()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String categoryName = resultSet.getString("category_name");
                        String name = resultSet.getString("name");
                        double price = resultSet.getDouble("price");
                        String specsJson = resultSet.getString("specification");
                        Map<String, String> specs = new HashMap<>();

                        if (specsJson != null && !specsJson.isEmpty()) {
                            JSONObject jsonObj = new JSONObject(specsJson);
                            for (String key : jsonObj.keySet()) {
                                specs.put(key, jsonObj.getString(key));
                            }
                        }

                        switch (categoryName) {
                            case "cloth":
                                if (!specs.isEmpty()) {
                                    Cloth cloth = new Cloth(id, name, categoryName, price, specs);
                                    for (Map.Entry<String, String> entry : specs.entrySet()) {
                                        cloth.addSpecification(entry.getKey(), entry.getValue());
                                    }
                                    products.add(cloth);
                                } else {
                                    Cloth cloth = new Cloth(id, name, categoryName, price);
                                    products.add(cloth);
                                }
                                break;
                            case "tech":
                                if (!specs.isEmpty()) {
                                    Technology tech = new Technology(id, name, categoryName, price, specs);
                                    for (Map.Entry<String, String> entry : specs.entrySet()) {
                                        tech.addSpecification(entry.getKey(), entry.getValue());
                                    }
                                    products.add(tech);
                                } else {
                                    Technology tech = new Technology(id, name, categoryName, price);
                                    products.add(tech);
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid category name: " + categoryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showLoginMenu() {
        System.out.println("\n=== Online Shopping Cart ===");
        System.out.println("=== Made by Burak Dursun, Muhammed Tunç and Yiğithan Karabel ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                registerPage();
                break;
            case 2:
                loginPage();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public void showMainMenu() {
        System.out.println("\n=== Welcome to Online Shopping Cart ===");
        System.out.println("1. View Products");
        System.out.println("2. Search Products");
        System.out.println("3. View Cart");
        System.out.println("4. Checkout");
        System.out.println("5. View Order History");
        System.out.println("6. Settings");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                showAllProducts();
                break;
            case 2:
                searchProducts();
                break;
            case 3:
                viewCart();
                break;
            case 4:
                checkoutPage();
                break;
            case 5:
                orderHistoryPage();
                break;
            case 6:
                settingsPage();
                break;
            case 7:
                this.loggedInUser = null;
                System.out.println("Logout successful!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    @Override
    public void loginPage() {
        System.out.println("\n=== Login ===");
        System.out.print("Email: ");
        String email = this.input.nextLine();
        System.out.print("Password: ");
        String password = this.input.nextLine();

        try {
            for (User user : this.users) {
                if (user.authenticate(email, password)) {
                    this.loggedInUser = user;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (this.loggedInUser != null) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    @Override
    public void registerPage() {
        System.out.println("\n=== Register ===");
        System.out.print("Name: ");
        String name = this.input.nextLine();
        System.out.print("Surname: ");
        String surname = this.input.nextLine();
        System.out.print("Email: ");
        String email = this.input.nextLine();
        System.out.print("Password: ");
        String password = this.input.nextLine();

        if (users.stream().anyMatch(user -> user.getEmail().equals(email))) {
            System.out.println("This email is already in use. Please try again.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = "INSERT INTO users (name, surname, email, secret_code) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, surname);
                statement.setString(3, email);
                statement.setString(4, User.hashPassword(password));
                statement.execute();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            User user = new User(name, surname, email, User.hashPassword(password));
            users.add(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Registration successful!");
    }

    public void showAllProducts() {
        System.out.println("\n=== Products List (All Products) ===");
        int index = 1;
        for (Product product : this.products) {
            System.out.println("ID: " + index++ + ", " + product.getDetails());
        }

        System.out.print("Enter product ID to add to cart (0 to go back): ");
        int id = getIntInput();
        if (id != 0) {
            try {
                if (id < 1 || id > this.products.size()) {
                    throw new IllegalArgumentException("No such product: " + id);
                }
                this.cart.addProduct(id, this.products);
                System.out.println("Product added to cart.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            showAllProducts();
        }
    }

    public void searchProducts() {
        System.out.println("\n=== Search Products ===");
        System.out.print("Enter search query: ");
        String query = this.input.nextLine().toLowerCase();
        System.out.println("\n==> Search Results:");
        for (Product product : this.products) {
            if (product.getName().toLowerCase().contains(query) || product.getCategory().toLowerCase().contains(query)) {
                System.out.println("ID: " + product.getId() + ", " + product.getDetails());
            }
        }
        System.out.print("Enter product ID to add to cart (0 to go back): ");
        int id = getIntInput();
        if (id != 0) {
            try {
                if (id < 1 || id > this.products.size()) {
                    throw new IllegalArgumentException("No such product: " + id);
                }
                this.cart.addProduct(id, this.products);
                System.out.println("Product added to cart.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void viewCart() {
        System.out.println("\n=== Cart Items ===");

        if (this.cart.getItems().isEmpty()) {
            System.out.println("Cart is empty. Please add items to cart.");
            return;
        }

        for (Product product : this.cart.getItems()) {
            System.out.println("ID: " + product.getId() + ", " + product.getDetails());
        }
        System.out.printf("Total Price: $%.2f\n", this.cart.getTotalPrice());

        System.out.print("Enter product ID to remove from cart (-1 to checkout, 0 to go back): ");
        int id = getIntInput();
        if (id == -1) {
            checkoutPage();
        } else if (id != 0) {
            try {

                Product productToRemove = null;
                for (Product product : this.cart.getItems()) {
                    if (product.getId() == id) {
                        productToRemove = product;
                        break;
                    }
                }

                if (productToRemove == null) {
                    throw new IllegalArgumentException("No such product in cart: " + id);
                }
                this.cart.removeProduct(productToRemove);
                System.out.println("Product removed from cart.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            viewCart();
        }
    }

    @Override
    public void checkoutPage() {
        System.out.println("\n=== Checkout ===");
        if (this.cart.getItems().isEmpty()) {
            System.out.println("Cart is empty. Please add items to cart.");
            return;
        }
        if (this.loggedInUser.getAddresses().isEmpty() || this.loggedInUser.getCardDetails().isEmpty()) {
            System.out.println("Please add address and card information in settings.");
            return;
        }
        
        for (Product product : this.cart.getItems()) {
            System.out.println("ID: " + product.getId() + ", " + product.getDetails());
        }

        System.out.println("Total Price: $" + this.cart.getTotalPrice() + "\n");

        for (int i = 0; i < this.loggedInUser.getCardDetails().size(); i++) {
            System.out.println((i + 1) + ". " + this.loggedInUser.getCardDetails().get(i));
        }

        System.out.print("Choose a card detail to use (0 to cancel): ");
        int cardDetailNumber = getIntInput();
        if (cardDetailNumber == 0) {
            return;
        }
        if (cardDetailNumber < 1 || cardDetailNumber > this.loggedInUser.getCardDetails().size()) {
            System.out.println("Invalid card detail number.");
            return;
        }

        System.out.println();

        for (int i = 0; i < this.loggedInUser.getAddresses().size(); i++) {
            System.out.println((i + 1) + ". " + this.loggedInUser.getAddresses().get(i));
        }

        System.out.print("Choose an address to use: ");
        int addressNumber = getIntInput();
        if (addressNumber < 1 || addressNumber > this.loggedInUser.getAddresses().size()) {
            System.out.println("Invalid address number.");
            return;
        }

        System.out.println();

        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            
            JSONArray jsonArray = new JSONArray();
            for (Product product : this.cart.getItems()) {
                jsonArray.put(product.getId());
            }

            String sql = "INSERT INTO shop_history (user_id, products, address_id, card_id) VALUES ((SELECT id FROM users WHERE email = ?), ?, (SELECT id FROM addresses WHERE user_id = (SELECT id FROM users WHERE email = ?) AND address = ? LIMIT 1), (SELECT id FROM cards WHERE user_id = (SELECT id FROM users WHERE email = ?) AND card_number = ? LIMIT 1))";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, this.loggedInUser.getEmail());
            statement.setString(2, jsonArray.toString());
            statement.setString(3, this.loggedInUser.getEmail());
            statement.setString(4, this.loggedInUser.getAddresses().get(addressNumber - 1));
            statement.setString(5, this.loggedInUser.getEmail());
            statement.setString(6, this.loggedInUser.getCardDetails().get(cardDetailNumber - 1).getCardNumber());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            int orderId = 0;
            if (resultSet.next()) {
                orderId = resultSet.getInt(1);
            } else {
                System.out.println("Failed to get order ID.");
            }

            Order order = new Order(orderId, this.cart.getItems());
            this.loggedInUser.addOrderHistory(order);
            this.cart = new Cart();
            System.out.println("Checkout successful! Order ID: " + order.getOrderId());
        } catch (Exception e) {
            System.out.println("Checkout failed. Error: " + e.getMessage());
        }
    }

    public void orderHistoryPage() {
        System.out.println("\n=== Order History ===");
        for (Order order : this.loggedInUser.getOrderHistory()) {
            System.out.println(order.toString());
        }
    }

    public void settingsPage() {
        System.out.println("\n=== Settings ===");
        System.out.println("1. Change Password");
        System.out.println("2. Add Address");
        System.out.println("3. Edit Addresses");
        System.out.println("4. Remove Addresses");
        System.out.println("5. Add Card Information");
        System.out.println("6. Edit Card Informations");
        System.out.println("7. Remove Card Informations");
        System.out.println("8. Back");
        System.out.print("Choose an option: ");
        int choice = getIntInput();
        switch (choice) {
            case 1:
                pageChangePassword();
                break;
            case 2:
                pageAddAddress();
                break;
            case 3:
                pageEditAddresses();
                break;
            case 4:
                pageRemoveAddresses();
                break;
            case 5:
                pageAddCardInfo();
                break;
            case 6:
                pageEditCardInfo();
                break;
            case 7:
                pageRemoveCardInfo();
                break;
            case 8:
                break;
            default:
                System.out.println("Invalid option.");
        }
        if (choice != 8) {
            settingsPage();            
        }
    }

    private void pageChangePassword() {
        
        System.out.println("\n==> Change Password");
        System.out.print("Enter old password: ");
        try {
            String oldPassword = this.input.nextLine();
            oldPassword = User.hashPassword(oldPassword);
            if (!oldPassword.equals(this.loggedInUser.getPassword())) {
                throw new Exception("Incorrect password.");
            }

            System.out.print("Enter new password: ");
            String newPassword = this.input.nextLine();

            System.out.print("Confirm new password: ");
            String confirmPassword = this.input.nextLine();
            if (!newPassword.equals(confirmPassword)) {
                throw new Exception("Passwords do not match.");
            }
            newPassword = User.hashPassword(newPassword);

            try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
                String sql = "UPDATE users SET secret_code = ? WHERE email = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, newPassword);
                statement.setString(2, this.loggedInUser.getEmail());
                statement.execute();
            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            }

            this.loggedInUser.setPassword(newPassword);
            for (User user : this.users) {
                if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                    user.setPassword(newPassword);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Password changed successfully.");
    }

    private void pageAddAddress() {
        System.out.println("\n==> Add Address");
        System.out.print("Enter address: ");
        String address = this.input.nextLine();

        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = "INSERT INTO addresses (user_id, address) VALUES ((SELECT id FROM users WHERE email = ?), ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.loggedInUser.getEmail());
            statement.setString(2, address);
            statement.execute();

            for (User user : this.users) {
                if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                    user.addAddress(address);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Address added successfully.");
    }

    private void pageEditAddresses() {
        System.out.println("\n==> Edit Addresses");
        this.loggedInUser.printAddresses();
        
        System.out.print("Choose an address to edit (0 to cancel): ");
        int addressIndex = getIntInput() - 1;
        if (addressIndex == -1) {
            return;
        }

        System.out.print("Enter new address: ");
        String newAddress = this.input.nextLine();
        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = "UPDATE addresses SET address = ? WHERE address = ? AND user_id = (SELECT id FROM users WHERE email = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newAddress);
            statement.setString(2, loggedInUser.getAddress(addressIndex));
            statement.setString(3, this.loggedInUser.getEmail());
            statement.execute();

            for (User user : this.users) {
                if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                    user.editAddress(addressIndex, newAddress);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Address edited successfully.");
        
        pageEditAddresses();
    }

    private void pageRemoveAddresses() {
        System.out.println("\n==> Remove Addresses");
        this.loggedInUser.printAddresses();
        System.out.print("Choose an address to remove (0 to cancel): ");
        int addressIndex = getIntInput() - 1;

        if (addressIndex == -1) {
            return;
        }
        
        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = "DELETE FROM addresses WHERE address = ? AND user_id = (SELECT id FROM users WHERE email = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, loggedInUser.getAddress(addressIndex));
            statement.setString(2, loggedInUser.getEmail());
            statement.execute();

            for (User user : this.users) {
                if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                    user.removeAddress(addressIndex);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Address removed successfully.");
        
        pageRemoveAddresses();
    }

    private void pageAddCardInfo() {
        try {
            System.out.println("\n==> Add Card Information");
            System.out.print("Enter card number (0 to cancel): ");
            String cardNumber = this.input.nextLine();
            if (cardNumber.equals("0")) {
                return;
            }
            if (cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")) {
                throw new IllegalArgumentException("Invalid card number.");
            }
            System.out.print("Enter expiration date (MM/YY): ");
            String expirationDate = this.input.nextLine();
            if (expirationDate.length() != 5) {
                throw new IllegalArgumentException("Invalid expiration date.");
            }
            System.out.print("Enter CVV: ");
            int cvv = this.input.nextInt();
            if (cvv < 100 || cvv > 999) {
                throw new IllegalArgumentException("Invalid CVV.");
            }
            CardDetail cardDetail = new CardDetail(CardDetail.encodeData(cardNumber), CardDetail.encodeData(Integer.toString(cvv)), CardDetail.encodeData(expirationDate));
            
            try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
                String sql = "INSERT INTO cards (user_id, card_number, expiry_date, cvv) VALUES ((SELECT id FROM users WHERE email = ?), ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, loggedInUser.getEmail());
                statement.setString(2, CardDetail.encodeData(cardNumber));
                statement.setString(3, CardDetail.encodeData(expirationDate));
                statement.setString(4, CardDetail.encodeData(Integer.toString(cvv)));
                statement.execute();

                for (User user : this.users) {
                    if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                        user.addCardDetail(cardDetail);
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Card information added successfully.");
    }

    private void pageEditCardInfo() {
        System.out.println("\n==> Edit Card Information");

        this.loggedInUser.printCardDetails();
        
        System.out.print("Choose a card detail to edit (0 to cancel): ");
        int addressIndex = getIntInput() - 1;

        if (addressIndex == -1) {
            return;
        }
        try {
            
            System.out.print("Enter card number: ");
            String newCardNumber = this.input.nextLine();
            if (newCardNumber.length() != 16 || !newCardNumber.matches("[0-9]+")) {
                throw new IllegalArgumentException("Invalid card number.");
            }
            System.out.print("Enter expiration date (MM/YY): ");
            String newExpirationDate = this.input.nextLine();
            if (newExpirationDate.length() != 5) {
                throw new IllegalArgumentException("Invalid expiration date.");
            }
            System.out.print("Enter CVV: ");
            int newCvv = this.input.nextInt();
            if (newCvv < 100 || newCvv > 999) {
                throw new IllegalArgumentException("Invalid CVV.");
            }

            try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
                String sql = "UPDATE cards SET card_number = ?, expiry_date = ?, cvv = ? WHERE card_number = ? AND user_id = (SELECT id FROM users WHERE email = ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, CardDetail.encodeData(newCardNumber));
                statement.setString(2, CardDetail.encodeData(newExpirationDate));
                statement.setString(3, CardDetail.encodeData(Integer.toString(newCvv)));
                statement.setString(4, this.loggedInUser.getCardNumber(addressIndex));
                statement.setString(5, this.loggedInUser.getEmail());
                statement.execute();

                CardDetail newCardDetail = new CardDetail(CardDetail.encodeData(newCardNumber), CardDetail.encodeData(Integer.toString(newCvv)), CardDetail.encodeData(newExpirationDate));

                for (User user : this.users) {
                    if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                        user.setCardDetail(addressIndex, newCardDetail);
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Card details edited successfully.");
        
        pageEditCardInfo();
    }

    private void pageRemoveCardInfo() {
        System.out.println("\n==> Remove Card Information");
        this.loggedInUser.printCardDetails();
        System.out.print("Choose a card detail to remove (0 to cancel): ");
        int addressIndex = getIntInput() - 1;

        if (addressIndex == -1) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DbConnect.getURL(), DbConnect.getUSER(), DbConnect.getPASSWORD())) {
            String sql = "DELETE FROM cards WHERE card_number = ? AND user_id = (SELECT id FROM users WHERE email = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.loggedInUser.getCardNumber(addressIndex));
            statement.setString(2, this.loggedInUser.getEmail());
            statement.execute();

            for (User user : this.users) {
                if (user.getEmail().equals(this.loggedInUser.getEmail())) {
                    user.removeCardDetail(addressIndex);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Card info removed successfully.");

        pageRemoveCardInfo();
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(this.input.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }
}
