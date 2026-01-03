package com.divinespark.service.impl;

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
}
