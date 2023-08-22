package com.spring.userservice.v1.api.auth;

import lombok.Getter;

@Getter
public class TokenDto {
    String accessToken;
    String refreshToken;

    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
