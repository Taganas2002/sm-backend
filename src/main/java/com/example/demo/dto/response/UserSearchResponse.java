package com.example.demo.dto.response;

import lombok.Data;

@Data
public class UserSearchResponse {
    private Long id;
    private String phone;
    private String email;
    private String Username;
}
