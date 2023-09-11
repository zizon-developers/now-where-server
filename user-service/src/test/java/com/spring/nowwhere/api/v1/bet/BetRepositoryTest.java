package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BetRepositoryTest extends IntegrationTestSupport {
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
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");

        int amount = 4500;
        Location location = new Location(454, 589);
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 2, 3);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 1, 2, 3);

        BetInfo betInfo = createBetInfo(amount,startTime,endTime,location);
        //when
        Bet saved = createBetAndSave(bettor, receiver, betInfo, BetStatus.PENDING);
        // then
        assertAll(
                () -> assertEquals(saved.getBettor(), bettor),
                () -> assertEquals(saved.getReceiver(), receiver),
                () -> assertEquals(saved.getBetStatus(), BetStatus.PENDING),

                () -> assertEquals(saved.getBetInfo().getAmount(), amount),
                () -> assertEquals(saved.getBetInfo().getStartTime(), startTime),
                () -> assertEquals(saved.getBetInfo().getEndTime(), endTime),
                () -> assertEquals(saved.getBetInfo().getAppointmentLocation(), location)
        );
    }

    @Test
    @DisplayName("완료된 내기가 아닌 목록들중에 시작시간과 종료시간에 포함되는 내기 목록을 조회할 수 있다.")
    public void findUncompletedBetsInTimeRange() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        Location location = new Location(454, 589);

        LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 59);
        int amount = 4500;
        BetInfo betInfo1 = createBetInfo(amount, startTime1, endTime1, location);

        LocalDateTime startTime2 = LocalDateTime.of(2021, 2, 6, 00, 00);
        LocalDateTime endTime2 = LocalDateTime.of(2021, 2, 6, 00, 10);
        BetInfo betInfo2 = createBetInfo(amount, startTime2, endTime2, location);

        createBetAndSave(bettor, receiver, betInfo1, BetStatus.ONGOING);
        createBetAndSave(bettor, receiver, betInfo2, BetStatus.PENDING);

        // when
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 55);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 6, 00, 7);
        Optional<Bet> bet = betRepository.findBetsInTimeRange(bettor, startTime, endTime);
        // then
        assertThat(bet.isPresent()).isTrue();
    }



//    @Test
//    @DisplayName("사용자가 신청한 내기를 모두 조회할 수 있다.")
//    public void findBetByBettor() {
//        // given
//        User bettor = createUserAndSave("bettor");
//        User receiver = createUserAndSave("receiver");
//
//        userRepository.saveAll(List.of(bettor,receiver));
//        LocalDateTime startTime;
//        LocalDateTime endTime;
//        int amount;
//        Location appointmentLocation;
//
//        Bet bet1 = Bet.builder()
//                .bettor(bettor)
//                .receiver(receiver)
//                (4500)
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

    private Bet createBetAndSave(User bettor, User receiver, BetInfo betInfo, BetStatus betStatus) {
        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(betStatus)
                .betInfo(betInfo)
                .build();
        return betRepository.save(bet);
    }

    private BetInfo createBetInfo(int amount, LocalDateTime startTime, LocalDateTime endTime, Location location) {
        return BetInfo.builder()
                .amount(amount)
                .startTime(startTime)
                .endTime(endTime)
                .appointmentLocation(location)
                .build();
    }
    private User createUserAndSave(String name) {
        User user = User.builder()
                .checkId(name+"id")
                .email(name+"@test.com")
                .name(name)
                .build();
        return userRepository.save(user);
    }
}