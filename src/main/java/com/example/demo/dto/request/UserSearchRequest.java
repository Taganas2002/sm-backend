package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String query; // Partial phone number or email.
}
