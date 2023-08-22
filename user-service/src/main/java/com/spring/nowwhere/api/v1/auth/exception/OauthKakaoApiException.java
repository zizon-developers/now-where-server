package com.spring.nowwhere.api.v1.auth.exception;

public class OauthKakaoApiException extends RuntimeException{
    public OauthKakaoApiException() {
        super();
    }

    public OauthKakaoApiException(String message) {
        super(message);
    }

    public OauthKakaoApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OauthKakaoApiException(Throwable cause) {
        super(cause);
    }

    protected OauthKakaoApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
