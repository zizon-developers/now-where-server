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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final int BETTOR_INDEX = 0;
    private final int RECEIVER_INDEX = 1;


    public ResponseBet createBet(String bettorId, RequestBet requestBet) {

        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, requestBet);
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetInfo betInfo = requestBet.getBetInfo();
        LocalDateTime startTime = betInfo.getStartTime();
        LocalDateTime endTime = betInfo.getEndTime();

        validateTimeRange(startTime,endTime);
        validationTimeRange(bettor, startTime, endTime);

        return ResponseBet.of(saveBet(bettor, receiver, betInfo));
    }

    private Bet saveBet(User bettor, User receiver, BetInfo betInfo) {
        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(BetStatus.REQUESTED)
                .betInfo(betInfo)
                .build();
        return betRepository.save(bet);
    }

    private List<User> checkBettorAndReceiver(String bettorId, RequestBet requestBet) {
        List<User> bettorAndReceiver = userRepository
                .findSenderAndReceiver(bettorId, requestBet.getReceiverId());
        if (bettorAndReceiver.size() != 2)
            throw new UsernameNotFoundException("bettor and receiver is not found");
        return bettorAndReceiver;
    }

    private void validationTimeRange(User bettor, LocalDateTime startTime, LocalDateTime endTime) {
        betRepository.findBetsInTimeRange(bettor, startTime, endTime)
                .ifPresent((ex)->{
                    throw new TimeValidationException("이미 시간에 포함된 내기가 있습니다.");
                });
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime){
        Duration duration = Duration.between(startTime, endTime)
                                    .minusMinutes(5);
        if (duration.toMinutes() < 0)
            throw new TimeValidationException("내기의 시작시간과 끝나는 시간의 차이는 5분 이상이어야 합니다.");
    }
}
