package com.smarthomes.controllers;

import com.smarthomes.models.Category;
import com.smarthomes.models.Product;
import com.smarthomes.services.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "CategoryController", urlPatterns = { "/api/categories/*" })
public class CategoryController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CategoryController.class.getName());
    private ProductService productService = new ProductService();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        try {
            handleGetAllCategories(resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving products.");
        }
    }

    private void handleGetAllCategories(HttpServletResponse resp) throws SQLException, IOException {
        List<Category> categories = productService.getAllCategories();
        JSONArray categoriesJson = new JSONArray();
        for (Category category : categories) {
            JSONObject categoryJson = new JSONObject();
            categoryJson.put("categoryId", category.getCategoryId());
            categoryJson.put("name", category.getName());
            categoriesJson.put(categoryJson);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(categoriesJson.toString());
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}

