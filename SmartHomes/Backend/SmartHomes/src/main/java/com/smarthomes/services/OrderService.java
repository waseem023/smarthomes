package com.smarthomes.services;

import com.smarthomes.dao.MySQLDataStoreUtilities;
import com.smarthomes.models.Order;
import com.smarthomes.models.Product;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private MySQLDataStoreUtilities db = new MySQLDataStoreUtilities();

    public void placeOrder(Order order) throws SQLException {
        db.placeOrder(order);
    }

    public List<Order> getAllorders() throws SQLException {
        return db.getAllOrders();
    }

    public List<Order> getorderById(int orderId) throws SQLException {
        return db.getOrdersByCustomerId(orderId);
    }

    // // Update a order
    // public void updateorder(Order order) throws SQLException {
    //     db.updateorder(order);
    // }

    // Delete a order by ID
    public boolean deleteorder(int orderId) throws SQLException {
        return db.deleteOrder(orderId);
    }
}
