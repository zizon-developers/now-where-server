package com.spring.nowwhere.api.v1.security;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private String uri;
    private String code;
    private String message;

    @Builder
    private ErrorResponse(String uri, String code, String message) {
        this.uri = uri;
        this.code = code;
        this.message = message;
    }
}
