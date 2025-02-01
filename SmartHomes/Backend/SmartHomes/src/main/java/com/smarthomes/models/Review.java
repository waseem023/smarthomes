package com.smarthomes.models;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Review {
    private String productModelName;
    private String productCategory;
    private double productPrice;
    private int userId;
    private int userAge;
    private String userGender;
    private String userOccupation;
    private int reviewRating;
    private String reviewText;
    private String reviewDate;
    private String storeName;
    private int storeZip;
    private String storeCity;
    private String storeState;
    private double manufacturerRebate;

    // Constructors, getters, and setters
    public Review(String productModelName, String productCategory, double productPrice, int userId,
     int reviewRating, String reviewText, String reviewDate,
            String storeName,
            int storeZip,
            String storeCity,
            String storeState,
            int userAge,
            String userGender,
            String userOccupation) {
        this.productModelName = productModelName;
        this.productCategory = productCategory;
        this.productPrice = productPrice;
        this.userId = userId;
        this.reviewRating = reviewRating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.storeName = storeName;
        this.storeZip = storeZip;
        this.storeCity = storeCity;
        this.storeState = storeState;
        this.manufacturerRebate = 0.0;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userOccupation = userOccupation;
    }

}
