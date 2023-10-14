package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BetQueryRepository {
    Optional<Bet> findTimeRangeAndDestination(User user, BetDateTime betDateTime, String destination);
    Optional<Bet> findBetInTimeRange(User bettor, User receiver, BetDateTime betDateTime);
    BetSummaryDto getUserBettingSummary(User user);
    List<Bet> findBetsByStartTime(LocalDateTime startTime);
    List<Bet> findBetsByEndTime(LocalDateTime endTime);
}
