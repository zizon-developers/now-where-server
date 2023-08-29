package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query("select b from Bet b where b.bettor = :user")
    List<Bet> findBetByBettor(@Param("user") User user);


    //나중에 진행상태 receiver가 수락했을 때는 동일하게 검증하기
    @Query("SELECT b FROM Bet b WHERE (:user in (b.bettor, b.receiver) AND b.betStatus not in(:status))" +
            "AND (b.betInfo.startTime <= :endTime AND b.betInfo.endTime >= :startTime)")
    List<Bet> findUncompletedBetsInTimeRange(@Param("user")User user,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       @Param("status") BetStatus status);
}
