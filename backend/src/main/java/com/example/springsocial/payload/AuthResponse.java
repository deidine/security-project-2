package com.example.springsocial.payload;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private int id;
    private String email, username;

    public AuthResponse(String accessToken, String username, int id, String email) {
        this.accessToken = accessToken;
        this.username = username;
        this.id = id;
        this.email = email;

    }

    public String getUsername( ) {
        return username;
    }

    public int getId( ) {
        return id;
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

    public void setId(int id) {
        this.id = id;
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
