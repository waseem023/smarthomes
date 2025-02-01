package com.smarthomes.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {
    private int transactionId;
    private int userId;
    private String customerName;
    private String customerAddress;
    private String city;
    private String state;
    private int zipcode;
    private String creditCardNumber;
    private int orderId;
    private String purchaseDate;
    private String shipDate;
    private int productId;
    private String productName;
    private int categoryId;
    private int quantity;
    private double price;
    private double shippingCost;
    private double discount;
    private double totalSales;
    private Integer storeId;
    private String status;
    private List<OrderItem> items;
    
}

