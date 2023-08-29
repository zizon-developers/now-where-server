package com.spring.nowwhere.api.v1.entity.bet.service;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetInfo;
import com.spring.nowwhere.api.v1.entity.bet.BetStatus;
import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.bet.dto.RequestBet;
import com.spring.nowwhere.api.v1.entity.bet.dto.ResponseBet;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;
    private final BetRepository betRepository;


    //bet상태가 대기거나 진행상태면 추가 못하게 하기 (약속 시간이 겹치지 않거나 이미 지나지 않으면 가능하게 check하기)
    public ResponseBet createBet(String bettorId, RequestBet requestBet) {

        List<User> bettorAndReceiver = userRepository
                .findSenderAndReceiver(bettorId, requestBet.getReceiverId());
        if (bettorAndReceiver.size() != 2)
            throw new UsernameNotFoundException("bettor and receiver is not found");

        User bettor = bettorAndReceiver.get(0);
        User receiver = bettorAndReceiver.get(1);

        BetInfo betInfo = requestBet.getBetInfo();
        LocalDateTime startTime = betInfo.getStartTime();
        LocalDateTime endTime = betInfo.getEndTime();
        validateTimeRange(startTime,endTime);

        List<Bet> bets = betRepository.findUncompletedBetsInTimeRange(bettor, startTime, endTime, BetStatus.COMPLETED);
        if (!bets.isEmpty())
            throw new TimeValidationException("이미 시간에 포함된 내기가 있습니다.");

        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(BetStatus.PENDING)
                .betInfo(requestBet.getBetInfo())
                .build();

        return ResponseBet.of(betRepository.save(bet));
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime){
        Duration duration = Duration.between(startTime, endTime)
                                    .minusMinutes(5);

        if (duration.toMinutes() < 0)
            throw new TimeValidationException("내기의 시작시간과 끝나는 시간의 차이는 5분 이상이어야 합니다.");
    }
}
