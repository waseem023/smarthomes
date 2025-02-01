package com.smarthomes.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.smarthomes.models.Review;

import org.bson.Document;
import com.mongodb.MongoException;

public class MongoDBDataStoreUtilities {

    private MongoClient mongoClient;

    public MongoDBDataStoreUtilities() {
        try {
            mongoClient = MongoClients.create("mongodb+srv://Waseem1:testewa@cluster0.a7zbb.mongodb.net");
            System.out.println("MongoDB connection established.");
        } catch (MongoException e) {
            System.err.println("Error establishing MongoDB connection: " + e.getMessage());
        }
    }

    public MongoCollection<Document> getProductReviewCollection() {
        try {
            MongoDatabase database = mongoClient.getDatabase("smarthomes");
            return database.getCollection("product_reviews");
        } catch (MongoException e) {
            System.err.println("Error getting collection: " + e.getMessage());
            return null;
        }
    }

    public void addReview(Review review) {
        try {
            MongoCollection<Document> collection = getProductReviewCollection();
            if (collection != null) {
                Document reviewDoc = new Document("productModelName", review.getProductModelName())
                        .append("productCategory", review.getProductCategory())
                        .append("productPrice", review.getProductPrice())
                        .append("userId", review.getUserId())
                        .append("storeName", review.getStoreName())
                        .append("storeCity", review.getStoreCity())
                        .append("storeState", review.getStoreState())
                        .append("storeZip", review.getStoreZip())
                        .append("productOnSale", "Yes")
                        .append("manufacturerName", review.getProductCategory())
                        .append("manufacturerRebate", review.getManufacturerRebate())
                        .append("userId", review.getUserId())
                        .append("userAge", review.getUserAge())
                        .append("userGender", review.getUserGender())
                        .append("userOccupation", review.getUserOccupation())
                        .append("reviewRating", review.getReviewRating())
                        .append("reviewText", review.getReviewText())
                        .append("reviewDate", review.getReviewDate());

                collection.insertOne(reviewDoc);
                System.out.println("Review inserted successfully.");
            }
        } catch (MongoException e) {
            System.err.println("Error inserting review: " + e.getMessage());
        }
    }

    public void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("MongoDB connection closed.");
            } catch (MongoException e) {
                System.err.println("Error closing MongoDB connection: " + e.getMessage());
            }
        }
    }
}
