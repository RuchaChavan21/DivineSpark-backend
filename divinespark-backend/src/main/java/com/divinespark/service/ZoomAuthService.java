package com.divinespark.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class ZoomAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zoom.account.id}")
    private String accountId;

    @Value("${zoom.client.id}")
    private String clientId;

    @Value("${zoom.client.secret}")
    private String clientSecret;

    public String getAccessToken() {

        String url = "https://zoom.us/oauth/token"
                + "?grant_type=account_credentials"
                + "&account_id=" + accountId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = clientId + ":" + clientSecret;
        byte[] encodedAuth =
                Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));

        headers.set("Authorization", "Basic " + new String(encodedAuth));

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        Map.class
                );

        return response.getBody().get("access_token").toString();
    }
}
