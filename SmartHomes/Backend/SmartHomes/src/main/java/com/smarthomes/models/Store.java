package com.smarthomes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private int storeId;
    private String name;
    private String street;
    private String city;
    private String state;
    private int zipcode;
}
