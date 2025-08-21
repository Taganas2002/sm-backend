package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String gender;
    private String profilePhoto;
}
