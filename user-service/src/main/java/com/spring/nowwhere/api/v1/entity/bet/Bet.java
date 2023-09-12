package com.spring.nowwhere.api.v1.entity.bet;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import com.spring.nowwhere.api.v1.entity.user.User;
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
    @JoinColumn(name = "bettor")
    private User bettor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private BetStatus betStatus;
    @Embedded
    private BetInfo betInfo;

    @Enumerated(EnumType.STRING)
    private BetResult betResult;
    @Builder
    private Bet(User bettor, User receiver, BetStatus betStatus, BetInfo betInfo) {
        this.bettor = bettor;
        this.receiver = receiver;
        this.betStatus = betStatus;
        this.betInfo = betInfo;
    }
    public void updateBetResult(BetResult betResult){
        if (BetStatus.COMPLETED.equals(this.betStatus)){
            this.betResult = betResult;
        }
    }
    public void updateBetStatus(BetStatus betStatus){
        this.betStatus = betStatus;
    }

}
