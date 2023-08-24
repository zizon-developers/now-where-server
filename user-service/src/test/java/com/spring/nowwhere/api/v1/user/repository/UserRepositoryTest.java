package com.spring.nowwhere.api.v1.user.repository;

import com.spring.nowwhere.api.v1.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.groups.Tuple.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown(){
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("두명의 사용자ID를 통해서 해당하는 두명의 사용자를 조회할 수 있다.")
    public void findBettorAndReceiver() {
        // given
        User bettor = User.builder()
                .email("bettor@test.com")
                .userId("bettorId")
                .name("bettor")
                .build();

        User receiver = User.builder()
                .email("receiver@test.com")
                .userId("receiverId")
                .name("receiver")
                .build();

        User test = User.builder()
                .email("test@test.com")
                .userId("testId")
                .name("test")
                .build();

        userRepository.saveAll(List.of(bettor,receiver,test));
        // when
        List<User> findUsers = userRepository.findBettorAndReceiver("bettorId", "receiverId");
        // then
        Assertions.assertThat(findUsers).hasSize(2)
                .extracting("email","userId","name")
                .containsExactlyInAnyOrder(
                        tuple("receiver@test.com","receiverId","receiver"),
                        tuple("bettor@test.com","bettorId","bettor")
                );
    }

}