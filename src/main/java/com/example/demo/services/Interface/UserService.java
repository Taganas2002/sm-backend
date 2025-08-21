package com.example.demo.services.Interface;

import java.util.List;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    
}
