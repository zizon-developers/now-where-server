package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class BetRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @AfterEach
    void tearDown(){
        betRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자 두명의 내기를 저장할 수 있다")
    public void save() {
        // given
        User bettor = User.builder()
                .email("bettor@test.com")
                .userId("bettor")
                .name("bettor")
                .build();

        User receiver = User.builder()
                .email("receiver@test.com")
                .userId("receiver")
                .name("receiver")
                .build();

        userRepository.saveAll(List.of(bettor,receiver));
        // when
        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .amount(4500)
                .status(BetStatus.PENDING)
                .build();
        Bet saved = betRepository.save(bet);
        // then
        Assertions.assertThat(saved.getBettor()).isEqualTo(bettor);
        Assertions.assertThat(saved.getReceiver()).isEqualTo(receiver);
        Assertions.assertThat(saved.getAmount()).isEqualTo(4500);
        Assertions.assertThat(saved.getStatus()).isEqualTo(BetStatus.PENDING);
    }
}