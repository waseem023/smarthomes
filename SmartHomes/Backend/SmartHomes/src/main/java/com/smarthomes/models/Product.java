package com.smarthomes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int productId;
    private String name;
    private String imageUrl;
    private double price;
    private String description;
    private int categoryId;
    private String warranty;
    private double specialDiscount;
    private double manufacturerRebate;
}
