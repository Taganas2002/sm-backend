package com.example.demo.services.Implementation;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.dto.mapper.UserMapper;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        LOGGER.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        LOGGER.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.toUserResponse(user);
    }



    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        LOGGER.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        
        // Update required fields via the mapper (e.g., username, email)
        userMapper.updateUserFromRequest(userRequest, user);
        
        // Update optional profile photo if provided and not empty
        if (userRequest.getProfilePhoto() != null && !userRequest.getProfilePhoto().trim().isEmpty()) {
            user.setProfilePhoto(userRequest.getProfilePhoto());
            LOGGER.info("Updated profilePhoto: {}", userRequest.getProfilePhoto());
        }
        
        // Update optional gender if provided and not empty
        if (userRequest.getGender() != null && !userRequest.getGender().trim().isEmpty()) {
            user.setGender(userRequest.getGender());
            LOGGER.info("Updated gender: {}", userRequest.getGender());
        }
        
        User updatedUser = userRepository.save(user);
        LOGGER.info("User with id {} updated successfully", id);
        return userMapper.toUserResponse(updatedUser);
    }



    @Override
    public void deleteUser(Long id) {
        LOGGER.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
        LOGGER.info("User with id {} deleted successfully", id);
    }
}
