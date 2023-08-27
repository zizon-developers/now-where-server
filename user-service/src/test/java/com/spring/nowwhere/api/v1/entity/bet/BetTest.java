package com.spring.nowwhere.api.v1.entity.bet;

import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BetTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @AfterEach
    void tearDown(){
        betRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("내기 상태가 완료상태인 경우 내기의 결과를 업데이트할 수 있다. ")
    public void updateBetResult() {
        // given
        User bettor = User.builder()
                .checkId("bettorId")
                .email("bettor@test.com")
                .name("bettor")
                .build();

        User receiver = User.builder()
                .checkId("receiverId")
                .email("receiver@test.com")
                .name("receiver").build();
        userRepository.saveAll(List.of(bettor, receiver));

        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(BetStatus.COMPLETED)
                .betInfo(BetInfo.builder()
                        .amount(4500)
                        .startTime(LocalDateTime.of(2021, 2, 3, 1, 2, 3))
                        .endTime(LocalDateTime.of(2021, 2, 5, 1, 2, 3))
                        .appointmentLocation(new Location(454, 589))
                        .build())
                .build();

        betRepository.save(bet);
        // when

        // then
    }

}