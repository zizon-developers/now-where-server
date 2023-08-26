package com.spring.nowwhere.api.v1.entity.bet;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bet extends BaseDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bettor_id")
    private User bettor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private BetStatus status;
    @Embedded
    private BetInfo betInfo;

    @Builder
    private Bet(User bettor, User receiver, BetStatus status, BetInfo betInfo) {
        this.bettor = bettor;
        this.receiver = receiver;
        this.status = status;
        this.betInfo = betInfo;
    }
}
