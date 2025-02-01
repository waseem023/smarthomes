package com.smarthomes.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.smarthomes.models.Category;
import com.smarthomes.models.Order;
import com.smarthomes.models.OrderItem;
import com.smarthomes.models.Product;
import com.smarthomes.models.Store;
import com.smarthomes.models.User;

public class MySQLDataStoreUtilities {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/OnlineRetailer?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "DBO@2024";

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public User addCustomer(User customer) throws SQLException, IllegalArgumentException {
        String checkEmailQuery = "SELECT COUNT(*) FROM Users WHERE email = ?";
        String insertQuery = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection()) {
            // Check if the email already exists
            try (PreparedStatement checkStmt = con.prepareStatement(checkEmailQuery)) {
                checkStmt.setString(1, customer.getEmail());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    rs.next();
                    int count = rs.getInt(1);
                    if (count > 0) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                }
            }

            // If email doesn't exist, insert the new customer and get the generated key
            // (user_id)
            try (PreparedStatement pstmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, customer.getName());
                pstmt.setString(2, customer.getEmail());
                pstmt.setString(3, customer.getPassword());
                pstmt.setString(4, "Customer");
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                // Retrieve the generated user_id
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        customer.setUserId(userId);
                        ; // Set the generated ID back to the customer object
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        }

        // Return the saved customer object with the user_id set
        return customer;
    }

    public User getCustomerByUsername(String email) throws SQLException {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"));
                }
            }
        }
        return null;
    }

    // Get all customers
    public List<User> getAllCustomers() throws SQLException {
        List<User> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'Customer'";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapUser(rs));
            }
        }
        return customers;
    }

    // Get a customer by ID
    public User getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        User customer = null;
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = mapUser(rs);
                }
            }
        }
        return customer;
    }

    // Update a customer
    public void updateCustomer(User customer) throws SQLException {
        String sql = "UPDATE Users SET name = ?, email = ? WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setInt(3, customer.getUserId());
            stmt.executeUpdate();
        }
    }

    // Delete a customer
    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map a ResultSet row to a User object
    private User mapUser(ResultSet rs) throws SQLException {
        User customer = new User();
        customer.setUserId(rs.getInt("user_id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPassword(rs.getString("password"));
        customer.setRole(rs.getString("role"));
        return customer;
    }

    // Add a new product
    public Product addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, price, description, category_id, warranty, special_discount, manufacturer_rebate,imageUrl) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategoryId());
            stmt.setString(5, product.getWarranty());
            stmt.setDouble(6, product.getSpecialDiscount());
            stmt.setDouble(7, product.getManufacturerRebate());
            stmt.setString(8, product.getImageUrl());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                product.setProductId(rs.getInt(1));
            }
        }
        return product;
    }

    // Get all products
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }
        return products;
    }

    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    public List<Store> getAllStores() throws SQLException {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT * FROM stores";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stores.add(mapStore(rs));
            }
        }
        return stores;
    }

    // Get a product by ID
    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = mapProduct(rs);
                }
            }
        }
        return product;
    }

    // Update a product
    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, description = ?, category_id = ?, warranty = ?, special_discount = ?, manufacturer_rebate = ?, imageUrl = ? WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategoryId());
            stmt.setString(5, product.getWarranty());
            stmt.setDouble(6, product.getSpecialDiscount());
            stmt.setDouble(7, product.getManufacturerRebate());
            stmt.setString(8, product.getImageUrl());
            stmt.setInt(9, product.getProductId());
            stmt.executeUpdate();
        }
    }

    // Delete a product by ID
    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map a ResultSet row to a Product object
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setWarranty(rs.getString("warranty"));
        product.setSpecialDiscount(rs.getDouble("special_discount"));
        product.setManufacturerRebate(rs.getDouble("manufacturer_rebate"));
        product.setImageUrl(rs.getString("imageUrl"));
        return product;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("category_name"));
        return category;
    }

    private Store mapStore(ResultSet rs) throws SQLException {
        Store store = new Store();
        store.setStoreId(rs.getInt("StoreId"));
        store.setName(rs.getString("StoreName"));
        store.setStreet(rs.getString("Street"));
        store.setCity(rs.getString("City"));
        store.setState(rs.getString("State"));
        store.setZipcode(rs.getInt("Zipcode"));
        return store;
    }

    public void placeOrder(Order order) throws SQLException {
        String insertTransactionQuery = "INSERT INTO Transactions (user_id, customer_name, customer_address, credit_card_number, " +
                "order_id, purchase_date, ship_date, quantity, price, shipping_cost, discount, total_sales, store_id, items, status, product_id, category, product_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertCustomerQuery = "INSERT INTO Customers (customer_name, street, city, state, zipcode, user_id, transaction_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection()) {
            // Disable auto-commit to handle both inserts as a transaction
            con.setAutoCommit(false);

            // Insert the order into the Transactions table
            try (PreparedStatement pstmtTransaction = con.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmtTransaction.setInt(1, order.getUserId());
                pstmtTransaction.setString(2, order.getCustomerName());
                pstmtTransaction.setString(3, order.getCustomerAddress());
                pstmtTransaction.setString(4, order.getCreditCardNumber());
                pstmtTransaction.setInt(5, order.getOrderId());
                pstmtTransaction.setString(6, order.getPurchaseDate());
                pstmtTransaction.setString(7, order.getShipDate());
                pstmtTransaction.setInt(8, order.getQuantity());
                pstmtTransaction.setDouble(9, order.getPrice());
                pstmtTransaction.setDouble(10, order.getShippingCost());
                pstmtTransaction.setDouble(11, order.getDiscount());
                pstmtTransaction.setDouble(12, order.getTotalSales());
                
                if (order.getStoreId() != null) {
                    pstmtTransaction.setInt(13, order.getStoreId());
                } else {
                    pstmtTransaction.setNull(13, java.sql.Types.INTEGER);
                }
                
                pstmtTransaction.setString(14, new JSONArray(order.getItems()).toString()); // Convert items to JSON
                pstmtTransaction.setString(15, order.getStatus());
                pstmtTransaction.setInt(16, order.getProductId());
                pstmtTransaction.setInt(17, order.getCategoryId());
                pstmtTransaction.setString(18, order.getProductName());

                // Execute update and get the generated transaction_id
                pstmtTransaction.executeUpdate();
                
                // Retrieve the generated transaction ID
                ResultSet rs = pstmtTransaction.getGeneratedKeys();
                if (rs.next()) {
                    int transactionId = rs.getInt(1);

                    // Insert the customer into the Customers table
                    try (PreparedStatement pstmtCustomer = con.prepareStatement(insertCustomerQuery)) {
                        pstmtCustomer.setString(1, order.getCustomerName());
                        pstmtCustomer.setString(2, order.getCustomerAddress());
                        pstmtCustomer.setString(3, order.getCity());
                        pstmtCustomer.setString(4, order.getState());
                        pstmtCustomer.setInt(5, order.getZipcode());
                        pstmtCustomer.setInt(6, order.getUserId());
                        pstmtCustomer.setInt(7, transactionId);

                        // Execute the customer insert
                        pstmtCustomer.executeUpdate();
                    }
                }

                // Commit the transaction
                con.commit();
            } catch (SQLException e) {
                // Rollback if there's an error
                con.rollback();
                throw e;
            } finally {
                // Restore auto-commit to true
                con.setAutoCommit(true);
            }
        }
    }

    // Get an order by ID
    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT * FROM Transactions WHERE transaction_id = ?";
        Order order = null;

        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = mapOrder(rs);
                }
            }
        }
        return order;
    }

    // Get all orders by a customer
    public List<Order> getOrdersByCustomerId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Transactions WHERE user_id = ?";

        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Transactions";

        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    public void updateOrderStatus(Order order, String status) throws SQLException {
        String sql = "UPDATE Transactions SET status = ? WHERE transaction_id = ?";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, order.getTransactionId());
            pstmt.executeUpdate();
        }
    }

    // Get top five zip codes where the maximum number of products are sold
    public JSONArray getTopZipCodes() throws SQLException {
        String query = "SELECT zipcode, COUNT(*) AS sales_count FROM customers GROUP BY zipcode ORDER BY sales_count DESC LIMIT 5";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            JSONArray zipCodesArray = new JSONArray();
            while (rs.next()) {
                JSONObject zipCode = new JSONObject();
                zipCode.put("zipcode", rs.getString("zipcode"));
                zipCode.put("sales_count", rs.getInt("sales_count"));
                zipCodesArray.put(zipCode);
            }
            return zipCodesArray;
        }
    }

    // Get top five most sold products regardless of rating
    public JSONArray getTopSoldProducts() throws SQLException {
        String query = "SELECT product_id, product_name, SUM(quantity) AS total_sold\n" + //
                        "FROM transactions\n" + //
                        "GROUP BY product_id, product_name\n" + //
                        "ORDER BY total_sold DESC\n" + //
                        "LIMIT 5;";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            JSONArray productsArray = new JSONArray();
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("product_id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("total_sold", rs.getInt("total_sold"));
                productsArray.put(product);
            }
            return productsArray;
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Transactions";

        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    // Delete an order by orderId
    public boolean deleteOrder(int orderId) throws SQLException {
        String query = "DELETE FROM Transactions WHERE order_id = ?";
        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();

        order.setTransactionId(rs.getInt("transaction_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerAddress(rs.getString("customer_address"));
        order.setCreditCardNumber(rs.getString("credit_card_number"));
        order.setOrderId(rs.getInt("order_id"));
        order.setStatus(rs.getString("status"));
        order.setPurchaseDate(rs.getString("purchase_date"));
        order.setShipDate(rs.getString("ship_date"));
        order.setQuantity(rs.getInt("quantity"));
        order.setPrice(rs.getDouble("price"));
        order.setShippingCost(rs.getDouble("shipping_cost"));
        order.setDiscount(rs.getDouble("discount"));
        order.setTotalSales(rs.getDouble("total_sales"));
        order.setStoreId(rs.getInt("store_id"));

        JSONArray itemsJson = new JSONArray(rs.getString("items"));
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < itemsJson.length(); i++) {
            JSONObject itemJson = itemsJson.getJSONObject(i);
            OrderItem item = new OrderItem();
            item.setProductId(itemJson.getInt("productId"));
            item.setName(itemJson.getString("name"));
            item.setQuantity(itemJson.getInt("quantity"));
            item.setPrice(itemJson.getDouble("price"));
            items.add(item);
        }
        order.setItems(items);

        return order;
    }
}
