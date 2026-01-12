package com.divinespark.service.impl;

import com.divinespark.dto.ZoomRegistrationResponse;
import com.divinespark.service.ZoomAuthService;
import com.divinespark.service.ZoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomServiceImpl implements ZoomService {

    private static final Logger log =
            LoggerFactory.getLogger(ZoomServiceImpl.class);

    private final ZoomAuthService zoomAuthService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zoom.api.base-url}")
    private String baseUrl;

    @Value("${zoom.enabled}")
    private boolean zoomEnabled;

    public ZoomServiceImpl(ZoomAuthService zoomAuthService) {
        this.zoomAuthService = zoomAuthService;
    }

    /**
     * Registers user for Zoom meeting.
     * Note: Zoom may NOT return join_url for authenticated meetings.
     * We use session.zoomLink for joining.
     */
    @Override
    public ZoomRegistrationResponse registerUser(
            String meetingId,
            String email,
            String firstName,
            String lastName) {

        log.info("Registering Zoom user | meetingId={} | email={}", meetingId, email);

        // MOCK MODE (DEV / TEST)
        if (!zoomEnabled) {
            ZoomRegistrationResponse mock = new ZoomRegistrationResponse();
            mock.setRegistrantId("MOCK-REG-" + meetingId);
            mock.setJoinUrl(null); // not used
            return mock;
        }

        // REAL ZOOM MODE
        String token = zoomAuthService.getAccessToken();
        String url = baseUrl + "/meetings/" + meetingId + "/registrants";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put(
                "first_name",
                (firstName == null || firstName.trim().isEmpty())
                        ? "Participant"
                        : firstName.trim()
        );
        body.put("last_name", "User");

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map responseBody = response.getBody();

        ZoomRegistrationResponse result = new ZoomRegistrationResponse();
        if (responseBody != null) {
            Object rid = responseBody.get("registrant_id");
            result.setRegistrantId(rid != null ? rid.toString() : null);
        }

        // join_url intentionally ignored
        return result;
    }
}
