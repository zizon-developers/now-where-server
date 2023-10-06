package com.spring.nowwhere.api.v1.batch.event;//package com.spring.nowwhere.api.v1.batch.event;

import com.spring.nowwhere.api.v1.entity.bet.Bet;

public class StartedBetEvent {
    private Bet bet;

    public StartedBetEvent(Bet bet) {
        this.bet = bet;
    }

    public Bet getBet() {
        return bet;
    }
}