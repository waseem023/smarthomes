package com.smarthomes.services;

import java.sql.SQLException;
import java.util.List;

import com.smarthomes.dao.MySQLDataStoreUtilities;
import com.smarthomes.models.User;

public class CustomerService {

    private MySQLDataStoreUtilities db = new MySQLDataStoreUtilities();

    // Register a new customer
    public User registerCustomer(User customer) throws SQLException {
       return db.addCustomer(customer);
    }

    // Login method
    public User login(String email, String password) throws SQLException {
        User customer = db.getCustomerByUsername(email);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }

    // Get all customers
    public List<User> getAllCustomers() throws SQLException {
        return db.getAllCustomers();
    }

    // Get a customer by ID
    public User getCustomerById(int customerId) throws SQLException {
        return db.getCustomerById(customerId);
    }

    // Update a customer
    public void updateCustomer(User customer) throws SQLException {
        db.updateCustomer(customer);
    }

    // Delete a customer by ID
    public boolean deleteCustomer(int customerId) throws SQLException {
        return db.deleteCustomer(customerId);
    }
}
