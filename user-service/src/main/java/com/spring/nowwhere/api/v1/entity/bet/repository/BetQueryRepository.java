package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BetQueryRepository {
    Optional<Bet> findBetsInTimeRange(User bettor, User receiver, BetDateTime betDateTime);
    BetSummaryDto getUserBettingSummary(User user);
}
