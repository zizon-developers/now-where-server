package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.user.dto.UserResponse;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;

    public Bet createBet(RequestBetDto requestBetDto) {
//        User findBettor = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("not found bettor"));
//
//        receiver
        return null;
    }
}
