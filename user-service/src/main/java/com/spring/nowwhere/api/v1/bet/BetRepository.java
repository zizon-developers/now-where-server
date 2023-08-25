package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query("select b from Bet b where b.bettor = :user")
    List<Bet> findBetByBettor(@Param("user") User user);
}
