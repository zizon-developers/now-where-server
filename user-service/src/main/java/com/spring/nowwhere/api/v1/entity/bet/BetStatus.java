package com.spring.nowwhere.api.v1.entity.bet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BetStatus {
    PENDING("대기상태"),
    ONGOING("진행상태"),
    COMPLETED("완료상태");
    private final String text;

    public String getText() {
        return text;
    }
}