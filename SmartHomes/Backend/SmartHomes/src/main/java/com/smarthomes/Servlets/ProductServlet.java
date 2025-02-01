package com.smarthomes.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    // Sample data for demonstration
    private static List<String> products = new ArrayList<>(Arrays.asList("Phone", "Laptop", "Tablet"));

    // Handles GET requests (Read)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("Product added: ");
    }

    // Handles POST requests (Create)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String product = req.getParameter("name");
        if (product != null && !product.isEmpty()) {
            products.add(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Product added: " + product);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid product data");
        }
    }
    
    // Handles DELETE requests (Delete)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String product = req.getParameter("name");
        if (products.remove(product)) {
            resp.getWriter().write("Product removed: " + product);
        } else {
            resp.getWriter().write("Product not found: " + product);
        }
    }
}
