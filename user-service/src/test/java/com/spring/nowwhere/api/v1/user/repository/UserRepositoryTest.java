package com.spring.nowwhere.api.v1.user.repository;

import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.groups.Tuple.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown(){
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("두명의 checkID를 통해서 해당하는 두명의 사용자를 조회할 수 있으며 bettor, receiver 순으로 조회된다.")
    public void findBettorAndReceiver() {
        // given
        User bettor = createUser("bettor");
        User receiver = createUser("receiver");
        User test = createUser("test");

        userRepository.saveAll(List.of(bettor,receiver,test));
        // when
        List<User> findUsers = userRepository.findBettorAndReceiver("bettorId", "receiverId");
        // then
        Assertions.assertThat(findUsers).hasSize(2)
                .extracting("email","checkId","name")
                .containsExactly( //순서 보장되어야함
                        tuple("bettor@test.com","bettorId","bettor"),
                        tuple("receiver@test.com","receiverId","receiver")
                );
    }

    @Test
    @DisplayName("checkId로 특정 사용자를 조회할 수 있다.")
    public void findByUserId() {
        // given
        User test = createUser("test");
        userRepository.save(test);
        // when
        User findUser = userRepository.findByCheckId(test.getCheckId()).get();
        // then
        Assertions.assertThat(test.getCheckId()).isEqualTo(findUser.getCheckId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getName()).isEqualTo(findUser.getName());
    }
    @Test
    @DisplayName("email 특정 사용자를 조회할 수 있다.")
    public void findByEmail() {
        // given
        User test = createUser("test");
        userRepository.save(test);
        // when
        User findUser = userRepository.findByEmail(test.getEmail()).get();
        // then
        Assertions.assertThat(test.getCheckId()).isEqualTo(findUser.getCheckId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getName()).isEqualTo(findUser.getName());
    }

//    @Test
    @DisplayName("모든 사용자를 조회할 수 있다.")
    public void findAll() {
        // given
        User bettor = createUser("bettor");
        User receiver = createUser("receiver");
        User test = createUser("test");

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

    @Test
    @DisplayName("이름으로 사용자를 조회할 수 있다.")
    public void findByName() {
        // given
        User test = createUser("test");
        userRepository.save(test);
        // when
        User findUser = userRepository.findByName(test.getName()).get();
        // then
        Assertions.assertThat(test.getCheckId()).isEqualTo(findUser.getCheckId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getName()).isEqualTo(findUser.getName());
    }
    @Test
    @DisplayName("송금ID로 사용자를 조회할 수 있다.")
    public void findByRemittanceId() {
        // given
        User test = createUser("test");
        userRepository.save(test);
        // when
        User findUser = userRepository.findByRemittanceId(test.getRemittanceId()).get();
        // then
        Assertions.assertThat(test.getCheckId()).isEqualTo(findUser.getCheckId());
        Assertions.assertThat(test.getEmail()).isEqualTo(findUser.getEmail());
        Assertions.assertThat(test.getRemittanceId()).isEqualTo(findUser.getRemittanceId());
    }

    private User createUser(String name) {
        User test = User.builder()
                .email(name+"@test.com")
                .checkId(name+"Id")
                .name(name)
                .remittanceId(name+"PayId")
                .build();
        return test;
    }
}