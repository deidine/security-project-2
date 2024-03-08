package com.example.springsocial.payload;

import java.util.List;

import com.example.springsocial.model.AppUserRole;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private List<AppUserRole> roles;
    private String email, username;

    public AuthResponse(String accessToken, String username, List<AppUserRole> roles, String email) {
        this.accessToken = accessToken;
        this.username = username;
        this.roles = roles;
        this.email = email;

    }

    public String getUsername( ) {
        return username;
    }

    public List<AppUserRole> getRoles( ) {
        return roles;
    }

    public String getEmail( ) {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(List<AppUserRole> roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
