package com.divinespark.service.impl;

import com.divinespark.dto.ZoomRegistrationResponse;
import com.divinespark.service.ZoomService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "zoom.mode", havingValue = "MOCK")
public class ZoomMockServiceImpl implements ZoomService {

    @Override
    public ZoomRegistrationResponse registerUser(
            String meetingId,
            String email,
            String firstName,
            String lastName) {

        ZoomRegistrationResponse response =
                new ZoomRegistrationResponse();

        response.setJoinUrl(
                "https://zoom.us/j/" + meetingId + "?mockUser=" + email
        );
        response.setRegistrantId("MOCK_" + UUID.randomUUID());

        return response;
    }
}
