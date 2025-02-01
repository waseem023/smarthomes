package com.smarthomes.controllers;

import com.smarthomes.dao.MySQLDataStoreUtilities;
import com.smarthomes.models.Order;
import com.smarthomes.models.OrderItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "OrderManagementController", urlPatterns = { "/api/orders/*" })
public class OrderManagementController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OrderManagementController.class.getName());
    private MySQLDataStoreUtilities dataStoreUtilities = new MySQLDataStoreUtilities();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetOrders(resp);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int orderId = Integer.parseInt(pathParts[1]);
                    handleGetOrderById(orderId, resp);
                } else if (pathParts.length == 3 && "customer".equals(pathParts[1])) {
                    int userId = Integer.parseInt(pathParts[2]);
                    handleGetOrdersByCustomerId(userId, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid path format.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving orders.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        try {
            handleCreateOrUpdateOrder(req, resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during order creation", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during order creation.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int orderId = Integer.parseInt(pathInfo.split("/")[1]);
                handleDeleteOrder(orderId, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid order ID format.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during order deletion", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during order deletion.");
        }
    }

    private void handleGetOrderById(int orderId, HttpServletResponse resp) throws SQLException, IOException {
        Order order = dataStoreUtilities.getOrderById(orderId);
        if (order != null) {
            JSONObject orderJson = convertOrderToJson(order);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(orderJson.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Order not found");
        }
    }

    private void handleGetOrdersByCustomerId(int userId, HttpServletResponse resp) throws SQLException, IOException {
        List<Order> orders = dataStoreUtilities.getOrdersByCustomerId(userId);
        JSONArray ordersJson = new JSONArray();
        for (Order order : orders) {
            ordersJson.put(convertOrderToJson(order));
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(ordersJson.toString());
    }

    private void handleGetOrders(HttpServletResponse resp) throws SQLException, IOException {
        List<Order> orders = dataStoreUtilities.getOrders();
        JSONArray ordersJson = new JSONArray();
        for (Order order : orders) {
            ordersJson.put(convertOrderToJson(order));
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(ordersJson.toString());
    }

    private void handleCreateOrUpdateOrder(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());

        // Extract order details from request
        int orderId = jsonObject.getInt("order_id");
        String status = jsonObject.optString("status", "Pending");

        // Check if the order already exists
        Order existingOrder = dataStoreUtilities.getOrderById(orderId);

        if (existingOrder != null) {
            existingOrder.setStatus(status);
            dataStoreUtilities.updateOrderStatus(existingOrder, status);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Order status updated successfully.");
        } else {
            int userId = jsonObject.getInt("user_id");
            String customerName = jsonObject.getString("customer_name");
            String customerAddress = jsonObject.getString("customer_address");
            String city = jsonObject.getString("city");
            String state = jsonObject.getString("state");
            Integer zipcode = jsonObject.getInt("zipcode");
            String creditCardNumber = jsonObject.getString("credit_card_number");
            String purchaseDate = jsonObject.getString("purchase_date"); 
            String shipDate = jsonObject.getString("ship_date"); 
            String productName = jsonObject.getString("product_name"); 
            int quantity = jsonObject.getInt("quantity");
            double price = jsonObject.getDouble("price");
            double shippingCost = jsonObject.getDouble("shipping_cost");
            double discount = jsonObject.getDouble("discount");
            double totalSales = jsonObject.getDouble("total_sales");
            Integer productId = jsonObject.has("product_id") && !jsonObject.isNull("product_id") ? jsonObject.getInt("product_id") : null;
            Integer category = jsonObject.has("category") && !jsonObject.isNull("category") ? jsonObject.getInt("category") : null;
            Integer storeId = jsonObject.has("store_id") && !jsonObject.isNull("store_id") ? jsonObject.getInt("store_id") : null;
    
            // Parse items from JSON
            JSONArray itemsJson = jsonObject.getJSONArray("items");
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < itemsJson.length(); i++) {
                JSONObject itemJson = itemsJson.getJSONObject(i);
                OrderItem item = new OrderItem();
                item.setProductId(itemJson.getInt("product_id"));
                item.setName(itemJson.getString("name"));
                item.setQuantity(itemJson.getInt("quantity"));
                item.setPrice(itemJson.getDouble("price"));
                items.add(item);
            }
            // Create new order
            Order order = new Order();
            order.setUserId(userId);
            order.setCustomerName(customerName);
            order.setCustomerAddress(customerAddress);
            order.setCreditCardNumber(creditCardNumber);
            order.setOrderId(orderId);
            order.setPurchaseDate(purchaseDate); 
            order.setShipDate(shipDate);
            order.setQuantity(quantity);
            order.setPrice(price);
            order.setShippingCost(shippingCost);
            order.setDiscount(discount);
            order.setTotalSales(totalSales);
            order.setStoreId(storeId);
            order.setItems(items);
            order.setStatus(status);
            order.setProductId(productId);
            order.setProductName(productName);
            order.setCategoryId(category);
            order.setCity(city);
            order.setState(state);
            order.setZipcode(zipcode);
            dataStoreUtilities.placeOrder(order);

            // Respond with the newly created order
            JSONObject responseJson = convertOrderToJson(order);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(responseJson.toString());
        }
    }

    private void handleDeleteOrder(int orderId, HttpServletResponse resp) throws SQLException, IOException {
        boolean deleted = dataStoreUtilities.deleteOrder(orderId);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Order not found");
        }
    }

    // Helper method to convert an Order object to JSON
    private JSONObject convertOrderToJson(Order order) {
        JSONObject orderJson = new JSONObject();
        orderJson.put("transaction_id", order.getTransactionId());
        orderJson.put("user_id", order.getUserId());
        orderJson.put("customer_name", order.getCustomerName());
        orderJson.put("customer_address", order.getCustomerAddress());
        orderJson.put("credit_card_number", order.getCreditCardNumber());
        orderJson.put("order_id", order.getOrderId());
        orderJson.put("purchase_date", order.getPurchaseDate());
        orderJson.put("ship_date", order.getShipDate());
        orderJson.put("quantity", order.getQuantity());
        orderJson.put("price", order.getPrice());
        orderJson.put("shipping_cost", order.getShippingCost());
        orderJson.put("discount", order.getDiscount());
        orderJson.put("total_sales", order.getTotalSales());
        orderJson.put("store_id", order.getStoreId());
        orderJson.put("status", order.getStatus());

        // Convert items to JSON
        JSONArray itemsJson = new JSONArray();
        for (OrderItem item : order.getItems()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("product_id", item.getProductId());
            itemJson.put("name", item.getName());
            itemJson.put("quantity", item.getQuantity());
            itemJson.put("price", item.getPrice());
            itemsJson.put(itemJson);
        }
        orderJson.put("items", itemsJson);

        return orderJson;
    }
    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
