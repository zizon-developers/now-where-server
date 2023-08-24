package com.spring.nowwhere.api.v1.user.repository;

import com.spring.nowwhere.api.v1.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

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
                .containsExactly( //순서 보장되어야함
                        tuple("bettor@test.com","bettorId","bettor"),
                        tuple("receiver@test.com","receiverId","receiver")
                );
    }

    @Test
    @DisplayName("UserId로 특정 사용자를 조회할 수 있다.")
    public void findByUserId() {
        // given
        User test = User.builder()
                .email("test@test.com")
                .userId("testId")
                .name("test")
                .build();
        userRepository.save(test);
        // when
        User findUser = userRepository.findByUserId(test.getUserId()).get();
        // then
        Assertions.assertThat(test.getUserId()).isEqualTo(findUser.getUserId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getName()).isEqualTo(findUser.getName());
    }
    @Test
    @DisplayName("email 특정 사용자를 조회할 수 있다.")
    public void findByEmail() {
        // given
        User test = User.builder()
                .email("test@test.com")
                .userId("testId")
                .name("test")
                .build();
        userRepository.save(test);
        // when
        User findUser = userRepository.findByEmail(test.getEmail()).get();
        // then
        Assertions.assertThat(test.getUserId()).isEqualTo(findUser.getUserId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getName()).isEqualTo(findUser.getName());
    }

    @Test
    @DisplayName("모든 사용자를 조회할 수 있다.")
    public void findAll() {
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
        List<User> findUsers = userRepository.findAll();
        // then
        Assertions.assertThat(findUsers).hasSize(3)
                .extracting("email","userId","name")
                .containsExactlyInAnyOrder( //순서 보장되어야함
                        tuple("bettor@test.com","bettorId","bettor"),
                        tuple("receiver@test.com","receiverId","receiver"),
                        tuple("test@test.com","testId","test")
                );
    }

}