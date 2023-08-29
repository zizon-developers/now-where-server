package com.spring.nowwhere.api.v1.entity.friend;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FriendStatus {
    PENDING("대기상태"),
    CANCELED_REQUEST("요청취소"),
    DENIED_REQUEST("요청거절"),
    COMPLETED("완료상태");
    private final String text;

    public String getText() {
        return text;
    }
}