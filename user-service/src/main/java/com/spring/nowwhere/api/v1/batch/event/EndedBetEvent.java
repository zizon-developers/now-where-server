package com.spring.nowwhere.api.v1.batch.event;//package com.spring.nowwhere.api.v1.batch.event;

import com.spring.nowwhere.api.v1.entity.bet.Bet;

public class EndedBetEvent {
    private Bet bet;

    public EndedBetEvent(Bet bet) {
        this.bet = bet;
    }

    public Bet getBet() {
        return bet;
    }
}
