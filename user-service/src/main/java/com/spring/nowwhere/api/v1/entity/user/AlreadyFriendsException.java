package com.spring.nowwhere.api.v1.entity.user;

public class AlreadyFriendsException extends RuntimeException{
    public AlreadyFriendsException() {
        super();
    }

    public AlreadyFriendsException(String message) {
        super(message);
    }

    public AlreadyFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFriendsException(Throwable cause) {
        super(cause);
    }

    protected AlreadyFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
