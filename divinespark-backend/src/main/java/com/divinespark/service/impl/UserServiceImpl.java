package com.divinespark.service.impl;

import com.divinespark.dto.UpdateProfileRequest;
import com.divinespark.dto.UserProfileResponse;
import com.divinespark.entity.User;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setContactNumber(user.getContactNumber());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getContactNumber() != null && !request.getContactNumber().isBlank()) {

            if (!request.getContactNumber().matches("^[6-9]\\d{9}$")) {
                throw new RuntimeException("Invalid contact number");
            }

            user.setContactNumber(request.getContactNumber());
        }

        userRepository.save(user);
    }


}
