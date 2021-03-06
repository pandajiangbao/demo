package com.panda.demo.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Panda
 * @date 4/20/2020
 */
@Slf4j
@RestController
@RequestMapping("oauth")
public class OauthController {
    @Value("${azure.activedirectory.tenant-id}")
    private String tenantId;
    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;

    @PostMapping("accessToken")
    public JSONObject getAccessToken(@RequestParam String code) {
        String host = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
        log.error(host);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", "api://cf9eb1d7-b952-478b-a0ed-e30b2de1f99e/App.All");
        body.add("redirect_uri", "http://localhost:8999");
        body.add("grant_type", "authorization_code");
        body.add("client_secret", clientSecret);
        body.add("code", code);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        HttpEntity httpEntity = new HttpEntity<>(body,httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(host, httpEntity, JSONObject.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().equals(HttpStatus.OK)){
            return response.getBody();
        }
        return null;
    }

    @PostMapping("refreshToken")
    public JSONObject getRefreshToken(@RequestParam String refreshToken) {
        String host = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
        log.error(host);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", "api://cf9eb1d7-b952-478b-a0ed-e30b2de1f99e/App.All");
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");
        body.add("client_secret", clientSecret);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        HttpEntity httpEntity = new HttpEntity<>(body,httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(host, httpEntity, JSONObject.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().equals(HttpStatus.OK)){
            return response.getBody();
        }
        return null;
    }
}
