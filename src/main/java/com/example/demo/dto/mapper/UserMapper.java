package com.example.demo.dto.mapper;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserSearchResponse;
import com.example.demo.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    
    public UserSearchResponse toUserSearchResponse(User user) {
        UserSearchResponse dto = new UserSearchResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    public UserResponse toUserResponse(User user) {
        if(user == null) return null;
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setGender(user.getGender());
        response.setProfilePhoto(user.getProfilePhoto());
        response.setPin(user.getPin());
        return response;
    }
    
    public User toUser(UserRequest request) {
        if(request == null) return null;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return user;
    }
    public void updateUserFromRequest(UserRequest request, User user) {
        if(request == null || user == null) return;
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Phone is intentionally not updated
    }
}
