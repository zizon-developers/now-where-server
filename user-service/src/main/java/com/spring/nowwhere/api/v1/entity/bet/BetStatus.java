package com.spring.nowwhere.api.v1.entity.bet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BetStatus {
    REQUESTED("요청 상태"),
    WAITING("대기 상태"),
    IN_PROGRESS("진행 상태"),
    COMPLETED("완료상태");
    private final String text;

    public String getText() {
        return text;
    }
}