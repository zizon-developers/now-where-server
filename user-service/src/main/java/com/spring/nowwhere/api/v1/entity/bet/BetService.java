package com.spring.nowwhere.api.v1.entity.bet;

import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;
    private final BetRepository betRepository;


    //bet상태가 대기거나 진행상태면 추가 못하게 하기
    public ResponseBet createBet(String bettorId, RequestBet requestBet) {
        List<User> bettorAndReceiver = userRepository
                .findBettorAndReceiver(bettorId, requestBet.getReceiverId());
        if (bettorAndReceiver.size() != 2)
            throw new UsernameNotFoundException("bettor and receiver is not found");

        Bet bet = Bet.builder()
                .bettor(bettorAndReceiver.get(0))
                .receiver(bettorAndReceiver.get(1))
                .betStatus(BetStatus.PENDING)
                .betInfo(requestBet.getBetInfo())
                .build();

        return ResponseBet.of(betRepository.save(bet));
    }
}
