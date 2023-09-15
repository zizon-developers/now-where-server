package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BetQueryRepository {
    Optional<Bet> findBetsInTimeRange(User bettor, User receiver, LocalDateTime startTime, LocalDateTime endTime);
    BetSummaryDto getUserBettingSummary(User user);
}
