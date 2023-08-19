package com.spring.userservice.auth.exception;

public class KakaoFriendsException extends RuntimeException{
    public KakaoFriendsException() {
        super();
    }

    public KakaoFriendsException(String message) {
        super(message);
    }

    public KakaoFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoFriendsException(Throwable cause) {
        super(cause);
    }

    protected KakaoFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
