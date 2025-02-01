package com.smarthomes.services;

import com.smarthomes.dao.MySQLDataStoreUtilities;
import com.smarthomes.models.Category;
import com.smarthomes.models.Product;
import com.smarthomes.models.Store;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private MySQLDataStoreUtilities db = new MySQLDataStoreUtilities();

    // Add a new product
    public Product addProduct(Product product) throws SQLException {
        return db.addProduct(product);
    }

    // Get all products
    public List<Product> getAllProducts() throws SQLException {
        return db.getAllProducts();
    }

    public List<Category> getAllCategories() throws SQLException {
        return db.getAllCategories();
    }

    public List<Store> getAllStores() throws SQLException {
        return db.getAllStores();
    }

    // Get a product by ID
    public Product getProductById(int productId) throws SQLException {
        return db.getProductById(productId);
    }

    // Update a product
    public void updateProduct(Product product) throws SQLException {
        db.updateProduct(product);
    }

    // Delete a product by ID
    public boolean deleteProduct(int productId) throws SQLException {
        return db.deleteProduct(productId);
    }
}
