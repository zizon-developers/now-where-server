package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn()
    private User bettor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn()
    private User receiver;

    private int amount;

    @Enumerated(EnumType.STRING)
    private BetStatus status;

    @Builder
    private Bet(User bettor, User receiver, int amount, BetStatus status) {
        this.bettor = bettor;
        this.receiver = receiver;
        this.amount = amount;
        this.status = status;
    }
}
