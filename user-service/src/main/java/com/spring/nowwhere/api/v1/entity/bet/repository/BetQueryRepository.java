package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BetQueryRepository {

    //나중에 진행상태 receiver가 수락했을 때는 동일하게 검증하기
    Optional<Bet> findBetsInTimeRange(User user, LocalDateTime startTime, LocalDateTime endTime);
}
