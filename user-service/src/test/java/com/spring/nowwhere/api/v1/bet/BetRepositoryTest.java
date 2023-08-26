//package com.spring.nowwhere.api.v1.bet;
//
//import com.spring.nowwhere.api.v1.user.entity.User;
//import com.spring.nowwhere.api.v1.user.repository.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.assertj.core.groups.Tuple.*;
//
//@SpringBootTest
//class BetRepositoryTest {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private BetRepository betRepository;
//
//    @AfterEach
//    void tearDown(){
//        betRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
//
//    @Test
//    @DisplayName("사용자 두명의 내기를 저장할 수 있다")
//    public void save() {
//        // given
//        User bettor = User.builder()
//                .email("bettor@test.com")
//                .userId("bettor")
//                .name("bettor")
//                .build();
//
//        User receiver = User.builder()
//                .email("receiver@test.com")
//                .userId("receiver")
//                .name("receiver")
//                .build();
//
//        userRepository.saveAll(List.of(bettor,receiver));
//        // when
//        Bet bet = Bet.builder()
//                .bettor(bettor)
//                .receiver(receiver)
//                .amount(4500)
//                .status(BetStatus.PENDING)
//                .build();
//        Bet saved = betRepository.save(bet);
//        // then
//        Assertions.assertThat(saved.getBettor()).isEqualTo(bettor);
//        Assertions.assertThat(saved.getReceiver()).isEqualTo(receiver);
//        Assertions.assertThat(saved.getAmount()).isEqualTo(4500);
//        Assertions.assertThat(saved.getStatus()).isEqualTo(BetStatus.PENDING);
//    }
//
//    @Test
//    @DisplayName("사용자가 신청한 내기를 모두 조회할 수 있다.")
//    public void findBetByBettor() {
//        // given
//        User bettor = User.builder()
//                .email("bettor@test.com")
//                .userId("bettor")
//                .name("bettor")
//                .build();
//
//        User receiver = User.builder()
//                .email("receiver@test.com")
//                .userId("receiver")
//                .name("receiver")
//                .build();
//
//        userRepository.saveAll(List.of(bettor,receiver));
//
//        Bet bet1 = Bet.builder()
//                .bettor(bettor)
//                .receiver(receiver)
//                .amount(4500)
//                .status(BetStatus.PENDING)
//                .build();
//        Bet bet2 = Bet.builder()
//                .bettor(bettor)
//                .receiver(receiver)
//                .amount(5500)
//                .status(BetStatus.ONGOING)
//                .build();
//        Bet bet3 = Bet.builder()
//                .bettor(receiver)
//                .receiver(bettor)
//                .amount(6500)
//                .status(BetStatus.COMPLETED)
//                .build();
//        betRepository.saveAll(List.of(bet1,bet2,bet3));
//        // when
//        List<Bet> bets = betRepository.findBetByBettor(bettor);
//        // then
//        Assertions.assertThat(bets).hasSize(2)
//                .extracting("amount", "status", "bettor", "receiver")
//                .containsExactlyInAnyOrder(
//                        //session 에러 나옴 LAZY 조회라서 쿼리 수정하기  (지금 EAGER로 잠시 변경함) user equal메서드도 만들어줌
//                        tuple(4500,BetStatus.PENDING,bettor,receiver),
//                        tuple(5500,BetStatus.ONGOING,bettor,receiver)
//                );
//    }
//}