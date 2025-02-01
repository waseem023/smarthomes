package com.smarthomes.controllers;

import com.smarthomes.dao.MySQLDataStoreUtilities;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "TrendingController", urlPatterns = {"/api/trending"})
public class TrendingController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TrendingController.class.getName());
    private MySQLDataStoreUtilities dataStoreUtilities = new MySQLDataStoreUtilities();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        try {
            JSONObject trendingData = new JSONObject();

            JSONArray topZipCodes = dataStoreUtilities.getTopZipCodes();
            JSONArray mostSoldProducts = dataStoreUtilities.getTopSoldProducts();

            // Add each result set to the final response
            trendingData.put("mostLikedProducts", mostSoldProducts);
            trendingData.put("topZipCodes", topZipCodes);
            trendingData.put("mostSoldProducts", mostSoldProducts);

            sendResponse(resp, trendingData);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving trending data.");
        }
    }

    private void sendResponse(HttpServletResponse resp, JSONObject data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(data.toString());
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
