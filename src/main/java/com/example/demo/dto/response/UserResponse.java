package com.example.demo.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String profilePhoto;
    private String gender;
    private String pin;
}
