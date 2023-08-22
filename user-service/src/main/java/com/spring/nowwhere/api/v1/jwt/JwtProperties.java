package com.spring.nowwhere.api.v1.jwt;

public interface JwtProperties {

    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER_STRING = "Authorization";
}
