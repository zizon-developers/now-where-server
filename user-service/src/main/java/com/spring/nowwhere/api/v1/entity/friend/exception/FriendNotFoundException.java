package com.spring.nowwhere.api.v1.entity.friend.exception;

public class FriendNotFoundException extends RuntimeException {
    public FriendNotFoundException() {
        super();
    }

    public FriendNotFoundException(String message) {
        super(message);
    }

    public FriendNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendNotFoundException(Throwable cause) {
        super(cause);
    }

    protected FriendNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
