package com.smarthomes.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.smarthomes.dao.MySQLDataStoreUtilities;

public class AnalyticsService {

    private MySQLDataStoreUtilities db = new MySQLDataStoreUtilities();

    // Get top-selling products analytics
    public String getTopSellingProducts() throws SQLException {
        // This is just a mock implementation.
        // Replace this with actual SQL query that fetches the top-selling products.
        Map<String, Integer> topProducts = new HashMap<>();
        topProducts.put("Smart Speaker", 120);
        topProducts.put("Smart Thermostat", 80);
        topProducts.put("Smart Doorbell", 60);

        JSONObject json = new JSONObject(topProducts);
        return json.toString();
    }

    // Get total revenue analytics
    public String getTotalRevenue() throws SQLException {
        // This is just a mock implementation.
        // Replace this with actual SQL query that calculates total revenue.
        Map<String, Double> revenueData = new HashMap<>();
        revenueData.put("totalRevenue", 52000.50);

        JSONObject json = new JSONObject(revenueData);
        return json.toString();
    }
}

