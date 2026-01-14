package com.divinespark.service;

import com.divinespark.dto.UserProfileResponse;

public interface UserService {
    long getTotalUsers();
    UserProfileResponse getUserProfile(Long userId);

}
