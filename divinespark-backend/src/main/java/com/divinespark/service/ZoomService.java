package com.divinespark.service;

import com.divinespark.dto.ZoomRegistrationResponse;

public interface ZoomService {

    ZoomRegistrationResponse registerUser(
            String meetingId,
            String email,
            String firstName,
            String lastName
    );
}
