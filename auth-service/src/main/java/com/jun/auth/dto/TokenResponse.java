package com.jun.auth.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class TokenResponse implements Serializable {
    private String token;
    private String refreshToken;
    
    public static TokenResponse of(String token, String refreshToken) {
        TokenResponse response = new TokenResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
