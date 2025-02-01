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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "CustomerManagementController", urlPatterns = { "/api/customers/*" })
public class CustomerManagementController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CustomerManagementController.class.getName());
    private CustomerService customerService = new CustomerService();

    public CustomerManagementController() {
        super();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllCustomers(resp);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int customerId = Integer.parseInt(pathParts[1]);
                    handleGetCustomerById(customerId, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid customer ID format.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving customers.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleCreateCustomer(req, resp);
            } else {
                String[] pathParts = pathInfo.split("/");
                 if (pathParts.length == 3 && "edit".equals(pathParts[1])) {
                    int userId = Integer.parseInt(pathParts[2]);
                    handleUpdateCustomer(userId, req, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid path format.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during customer creation", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during customer creation.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int customerId = Integer.parseInt(pathInfo.split("/")[1]);
                handleUpdateCustomer(customerId, req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid customer ID format.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during customer update", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during customer update.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int customerId = Integer.parseInt(pathInfo.split("/")[1]);
                handleDeleteCustomer(customerId, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid customer ID format.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during customer deletion", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during customer deletion.");
        }
    }

    private void handleGetAllCustomers(HttpServletResponse resp) throws SQLException, IOException {
        List<User> customers = customerService.getAllCustomers();
        JSONArray customersJson = new JSONArray();
        for (User customer : customers) {
            JSONObject customerJson = new JSONObject();
            customerJson.put("userId", customer.getUserId());
            customerJson.put("name", customer.getName());
            customerJson.put("email", customer.getEmail());
            customerJson.put("role", customer.getRole());
            customerJson.put("password", customer.getPassword());
            customersJson.put(customerJson);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(customersJson.toString());
    }

    private void handleGetCustomerById(int customerId, HttpServletResponse resp) throws SQLException, IOException {
        User customer = customerService.getCustomerById(customerId);
        if (customer != null) {
            JSONObject customerJson = new JSONObject();
            customerJson.put("userId", customer.getUserId());
            customerJson.put("name", customer.getName());
            customerJson.put("email", customer.getEmail());
            customerJson.put("role", customer.getRole());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(customerJson.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Customer not found");
        }
    }

    private void handleCreateCustomer(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");

        User customer = new User();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setRole("Customer");

        User createdCustomer = customerService.registerCustomer(customer);

        JSONObject responseJson = new JSONObject();
        responseJson.put("userId", createdCustomer.getUserId());
        responseJson.put("name", createdCustomer.getName());
        responseJson.put("email", createdCustomer.getEmail());
        responseJson.put("role", createdCustomer.getRole());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(responseJson.toString());
    }

    private void handleUpdateCustomer(int customerId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");

        User customer = customerService.getCustomerById(customerId);
        if (customer != null) {
            customer.setName(name);
            customer.setEmail(email);
            customerService.updateCustomer(customer);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Customer not found");
        }
    }

    private void handleDeleteCustomer(int customerId, HttpServletResponse resp) throws SQLException, IOException {
        boolean deleted = customerService.deleteCustomer(customerId);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Customer not found");
        }
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
