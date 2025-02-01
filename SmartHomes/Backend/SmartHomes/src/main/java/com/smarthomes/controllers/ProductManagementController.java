package com.smarthomes.controllers;

import com.smarthomes.models.Product;
import com.smarthomes.services.ProductService;
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

@WebServlet(name = "ProductManagementController", urlPatterns = { "/api/products/*" })
public class ProductManagementController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ProductManagementController.class.getName());
    private ProductService productService = new ProductService();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK); // Return OK for preflight OPTIONS requests
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllProducts(resp);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int productId = Integer.parseInt(pathParts[1]);
                    handleGetProductById(productId, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid product ID format.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving products.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                handleCreateProduct(req, resp);
            } else {
                String[] pathParts = pathInfo.split("/");
                 if (pathParts.length == 3 && "edit".equals(pathParts[1])) {
                    int productId = Integer.parseInt(pathParts[2]);
                    handleUpdateProduct(productId, req, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid path format.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during product creation", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during product creation.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int productId = Integer.parseInt(pathInfo.split("/")[1]);
                handleUpdateProduct(productId, req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid product ID format.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during product update", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during product update.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCORSHeaders(resp);
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int productId = Integer.parseInt(pathInfo.split("/")[1]);
                handleDeleteProduct(productId, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid product ID format.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during product deletion", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during product deletion.");
        }
    }

    private void handleGetAllProducts(HttpServletResponse resp) throws SQLException, IOException {
        List<Product> products = productService.getAllProducts();
        JSONArray productsJson = new JSONArray();
        for (Product product : products) {
            JSONObject productJson = new JSONObject();
            productJson.put("productId", product.getProductId());
            productJson.put("imageUrl", product.getImageUrl());
            productJson.put("name", product.getName());
            productJson.put("price", product.getPrice());
            productJson.put("description", product.getDescription());
            productJson.put("category", product.getCategoryId());
            productJson.put("warranty", product.getWarranty());
            productJson.put("specialDiscount", product.getSpecialDiscount());
            productJson.put("manufacturerRebate", product.getManufacturerRebate());
            productsJson.put(productJson);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(productsJson.toString());
    }

    private void handleGetProductById(int productId, HttpServletResponse resp) throws SQLException, IOException {
        Product product = productService.getProductById(productId);
        if (product != null) {
            JSONObject productJson = new JSONObject();
            productJson.put("productId", product.getProductId());
            productJson.put("imageUrl", product.getImageUrl());
            productJson.put("name", product.getName());
            productJson.put("price", product.getPrice());
            productJson.put("description", product.getDescription());
            productJson.put("category", product.getCategoryId());
            productJson.put("warranty", product.getWarranty());
            productJson.put("specialDiscount", product.getSpecialDiscount());
            productJson.put("manufacturerRebate", product.getManufacturerRebate());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(productJson.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Product not found");
        }
    }

    private void handleCreateProduct(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
        String name = jsonObject.getString("name");
        String imageUrl = jsonObject.getString("imageUrl");
        double price = jsonObject.getDouble("price");
        String description = jsonObject.getString("description");
        int category = jsonObject.getInt("category");
        String warranty = jsonObject.getString("warranty");
        double specialDiscount = jsonObject.getDouble("specialDiscount");
        double manufacturerRebate = jsonObject.getDouble("manufacturerRebate");

        Product product = new Product();
        product.setName(name);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategoryId(category);
        product.setWarranty(warranty);
        product.setSpecialDiscount(specialDiscount);
        product.setManufacturerRebate(manufacturerRebate);

        Product createdProduct = productService.addProduct(product);

        JSONObject responseJson = new JSONObject();
        responseJson.put("productId", createdProduct.getProductId());
        responseJson.put("name", createdProduct.getName());
        responseJson.put("price", createdProduct.getPrice());
        responseJson.put("description", createdProduct.getDescription());
        responseJson.put("imageUrl", createdProduct.getImageUrl());
        responseJson.put("category", createdProduct.getCategoryId());
        responseJson.put("warranty", createdProduct.getWarranty());
        responseJson.put("specialDiscount", createdProduct.getSpecialDiscount());
        responseJson.put("manufacturerRebate", createdProduct.getManufacturerRebate());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(responseJson.toString());
    }

    private void handleUpdateProduct(int productId, HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
        String name = jsonObject.getString("name");
        String imageUrl = jsonObject.getString("imageUrl");
        double price = jsonObject.getDouble("price");
        String description = jsonObject.getString("description");
        int category = jsonObject.getInt("category");
        String warranty = jsonObject.getString("warranty");
        double specialDiscount = jsonObject.getDouble("specialDiscount");
        double manufacturerRebate = jsonObject.getDouble("manufacturerRebate");

        Product product = productService.getProductById(productId);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategoryId(category);
            product.setWarranty(warranty);
            product.setImageUrl(imageUrl);
            product.setSpecialDiscount(specialDiscount);
            product.setManufacturerRebate(manufacturerRebate);
            productService.updateProduct(product);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Product not found");
        }
    }

    private void handleDeleteProduct(int productId, HttpServletResponse resp) throws SQLException, IOException {
        boolean deleted = productService.deleteProduct(productId);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Product not found");
        }
    }

    private void addCORSHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}

