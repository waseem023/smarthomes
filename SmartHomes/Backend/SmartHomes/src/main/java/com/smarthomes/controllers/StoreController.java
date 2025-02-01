package com.smarthomes.controllers;

import com.smarthomes.models.Category;
import com.smarthomes.models.Product;
import com.smarthomes.models.Store;
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

@WebServlet(name = "StoreController", urlPatterns = { "/api/stores/*" })
public class StoreController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(StoreController.class.getName());
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
            handleGetAllStores(resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving products.");
        }
    }

    private void handleGetAllStores(HttpServletResponse resp) throws SQLException, IOException {
        List<Store> stores = productService.getAllStores();
        JSONArray storesJson = new JSONArray();
        for (Store store : stores) {
            JSONObject storeJson = new JSONObject();
            storeJson.put("storeId", store.getStoreId());
            storeJson.put("name", store.getName());
            storeJson.put("street", store.getStreet());
            storeJson.put("city", store.getCity());
            storeJson.put("state", store.getState());
            storeJson.put("zipcode", store.getZipcode());
            storesJson.put(storeJson);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(storesJson.toString());
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}

