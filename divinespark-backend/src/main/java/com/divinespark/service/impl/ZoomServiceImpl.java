package com.divinespark.service.impl;

import com.divinespark.dto.ZoomRegistrationResponse;
import com.divinespark.service.EmailService;
import com.divinespark.service.ZoomAuthService;
import com.divinespark.service.ZoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ZoomServiceImpl implements ZoomService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final ZoomAuthService zoomAuthService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zoom.api.base-url}")
    private String baseUrl;

    public ZoomServiceImpl(ZoomAuthService zoomAuthService) {
        this.zoomAuthService = zoomAuthService;
    }

    @Override
    public ZoomRegistrationResponse registerUser(String meetingId, String email, String firstName, String lastName) {

        String token = zoomAuthService.getAccessToken();

        String url = baseUrl + "/meetings/" + meetingId + "/registrants";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        body.put("email", email);

        String safeFirstName =
                (firstName == null || firstName.trim().isEmpty())
                        ? "Participant"
                        : firstName.trim();

        body.put("first_name", safeFirstName);
        body.put("last_name", "User");


        HttpEntity<Map<String, Object>> request
                = new HttpEntity<>(body, headers);

        log.error("ZOOM REQUEST BODY => {}", body);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map responseBody = response.getBody();

        ZoomRegistrationResponse result = new ZoomRegistrationResponse();
        result.setRegistrantId(responseBody.get("registrant_id").toString());
        result.setJoinUrl(responseBody.get("join_url").toString());

        return result;
    }
}
