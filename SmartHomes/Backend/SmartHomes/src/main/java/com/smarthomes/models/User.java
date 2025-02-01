package com.smarthomes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;
    
}