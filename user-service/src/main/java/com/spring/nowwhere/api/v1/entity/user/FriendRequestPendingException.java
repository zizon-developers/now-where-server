package com.spring.nowwhere.api.v1.entity.user;

public class FriendRequestPendingException extends RuntimeException {
    public FriendRequestPendingException() {
        super();
    }

    public FriendRequestPendingException(String message) {
        super(message);
    }

    public FriendRequestPendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendRequestPendingException(Throwable cause) {
        super(cause);
    }

    protected FriendRequestPendingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
