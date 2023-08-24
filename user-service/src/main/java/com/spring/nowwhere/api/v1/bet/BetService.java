package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;
    private final BetRepository betRepository;

    public ResponseBet createBet(String bettorId, RequestBet requestBet) {
        List<User> bettorAndReceiver = userRepository
                .findBettorAndReceiver(bettorId, requestBet.getReceiverId());
        if (bettorAndReceiver.size() != 2)
            throw new UsernameNotFoundException("bettor and receiver is not found");

        Bet build = Bet.builder()
                .bettor(bettorAndReceiver.get(0))
                .receiver(bettorAndReceiver.get(1))
                .amount(requestBet.getAmount())
                .status(BetStatus.PENDING)
                .build();

        return ResponseBet.of(betRepository.save(build));
    }
}
