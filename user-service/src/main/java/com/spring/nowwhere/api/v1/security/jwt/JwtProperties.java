package com.spring.nowwhere.api.v1.security.jwt;

public interface JwtProperties {

    String ACCESS_TOKEN = "Access-Token";
    String REFRESH_TOKEN = "Refresh-Token";

    String TOKEN_PREFIX = "Bearer ";
    String AUTHORIZATION = "Authorization";
}
