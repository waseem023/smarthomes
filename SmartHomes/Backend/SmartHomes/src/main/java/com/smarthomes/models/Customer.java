package com.smarthomes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String password;
}