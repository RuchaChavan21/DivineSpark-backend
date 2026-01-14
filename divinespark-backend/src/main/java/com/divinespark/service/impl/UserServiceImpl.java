package com.divinespark.service.impl;

import com.divinespark.dto.UserProfileResponse;
import com.divinespark.entity.User;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.UserService;
import org.springframework.stereotype.Service;

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
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

}
