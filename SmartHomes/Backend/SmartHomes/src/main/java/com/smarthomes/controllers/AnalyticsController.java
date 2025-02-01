package com.smarthomes.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.smarthomes.services.AnalyticsService;

import java.io.IOException;

@WebServlet("/api/analytics/*")
public class AnalyticsController extends HttpServlet {

    private AnalyticsService analyticsService = new AnalyticsService();

    // Handle top-selling products analytics
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // e.g., "/top-products" or "/revenue"
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid analytics endpoint.");
            return;
        }

        try {
            switch (pathInfo) {
                case "/top-products":
                    // Get top-selling products analytics
                    String topProductsJson = analyticsService.getTopSellingProducts();
                    resp.setContentType("application/json");
                    resp.getWriter().write(topProductsJson);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;

                case "/revenue":
                    // Get total revenue analytics
                    String revenueJson = analyticsService.getTotalRevenue();
                    resp.setContentType("application/json");
                    resp.getWriter().write(revenueJson);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;

                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown analytics endpoint.");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching analytics.");
        }
    }
}

