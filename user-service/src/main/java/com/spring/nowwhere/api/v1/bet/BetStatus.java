package com.spring.nowwhere.api.v1.bet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum BetStatus {
    PENDING("대기상태"),
    ONGOING("진행상태"),
    COMPLETED("완료상태");

    private final String text;
}