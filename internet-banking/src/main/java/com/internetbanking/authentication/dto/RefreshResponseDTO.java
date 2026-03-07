package com.internetbanking.authentication.dto;

public class RefreshResponseDTO {
    private final String accessToken;
    public RefreshResponseDTO(String accessToken) { this.accessToken = accessToken; }
    public String getAccessToken() { return accessToken; }
}