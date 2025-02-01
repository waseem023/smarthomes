package com.smarthomes.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.smarthomes.dao.MongoDBDataStoreUtilities;
import com.smarthomes.models.Review;

import java.util.logging.Logger;

import org.json.JSONObject;

import java.util.logging.Level;

@WebServlet("/api/reviews")
public class ReviewController extends HttpServlet {


    private static final Logger logger = Logger.getLogger(ReviewController.class.getName());
    private MongoDBDataStoreUtilities db = new MongoDBDataStoreUtilities();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        try {

             StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
            // Log the start of the request
            logger.info("Received POST request to add a review");

            // Extract request parameters
            String productModelName = jsonObject.getString("productModelName");
            String productCategory = jsonObject.getString("productCategory");
            double productPrice = jsonObject.getDouble("productPrice"); 
            int userId = jsonObject.getInt("userId");
            int reviewRating = jsonObject.getInt("reviewRating");
            String reviewText = jsonObject.getString("reviewText");
            int userAge = jsonObject.getInt("userAge");
            String userGender = jsonObject.getString("userGender");
            String userOccupation = jsonObject.getString("userOccupation");
            String storeName = jsonObject.getString("storeName");
            String storeCity = jsonObject.getString("storeCity");
            int storeZip = jsonObject.getInt("storeZip");
            String storeState = jsonObject.getString("storeState");

            logger.log(Level.FINE, "Review details - Product Model: {0}, Category: {1}, Price: {2}, User ID: {3}, Rating: {4}", 
                    new Object[] { productModelName, productCategory, productPrice, userId, reviewRating });
            // Log request parameters

            // Get the current date
            String reviewDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Create a Review object
            Review review = new Review(productModelName, productCategory, productPrice, userId, reviewRating, reviewText,
                    reviewDate, storeName, storeZip, storeCity, storeState,
                    userAge,
                    userGender,
                    userOccupation);

            // Insert review into MongoDB
            db.addReview(review);

            // Log success
            logger.info("Review added successfully for product: " + productModelName);

            // Send response
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Review submitted successfully\"}");
        } catch (Exception e) {
            // Log any exceptions
            logger.log(Level.SEVERE, "Error while submitting review", e);

            // Return error response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\":\"Error submitting review\"}");
        }
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
