package com.smarthomes.controllers;

import com.smarthomes.models.User;
import com.smarthomes.services.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

@WebServlet(name = "AuthController", urlPatterns = { "/api/auth/register", "/api/auth/login" })
public class AuthController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private CustomerService customerService = new CustomerService();

    public AuthController() {
        super();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String path = req.getServletPath();
        try {
            if (path.equals("/api/auth/register")) {
                handleRegistration(req, resp);
            } else if (path.equals("/api/auth/login")) {
                handleLogin(req, resp);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred: ", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request.");
        }
    }

    private void handleRegistration(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        try {
            // Parse JSON using org.json.JSONObject
            JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
            String name = jsonObject.getString("name");
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");

            logger.log(Level.INFO, "Register request for: " + email);
            User customer = new User();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setRole("Customer");
            User resultUser = customerService.registerCustomer(customer);
            JSONObject responseJson = new JSONObject();
            responseJson.put("name", resultUser.getName());
            responseJson.put("email", resultUser.getEmail());
            responseJson.put("role", resultUser.getRole());
            responseJson.put("userId", resultUser.getUserId());
            // Send success response with JSON object
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(responseJson.toString());

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Registration failed: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during registration", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during customer registration.");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");
        try {
            User customer = customerService.login(email, password);
            if (customer != null) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("name", customer.getName());
                responseJson.put("email", customer.getEmail());
                responseJson.put("role", customer.getRole());
                responseJson.put("userId", customer.getUserId());
                // Send success response with JSON object
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(responseJson.toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid username or password");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during login", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during login.");
        }
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
