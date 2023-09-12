package com.spring.nowwhere.api.v1.bet;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.bet.dto.QBetSummaryDto;
import com.spring.nowwhere.api.v1.entity.bet.dto.QUserInfoDto;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.bet.dto.UserInfoDto;
import com.spring.nowwhere.api.v1.entity.user.QUser;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.spring.nowwhere.api.v1.entity.bet.QBet.bet;
import static com.spring.nowwhere.api.v1.entity.user.QUser.user;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BetRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

//    @AfterEach
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
    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("사용자가 진행한 내기 횟수와, 내기로 번 금액을 얻을 수 있다.")
    public void getUserBettingSummary() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        User test = createUserAndSave("test");

        Location location = new Location(454, 589);
        LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetInfo betInfo1 = createBetInfo(4000, startTime1, endTime1, location);
        BetInfo betInfo2 = createBetInfo(5000, startTime1.plusDays(1), endTime1.plusDays(1), location);
        BetInfo betInfo3 = createBetInfo(6000, startTime1.plusDays(1), endTime1.plusDays(1), location);

        Bet bet1 = createBetAndSave(bettor, receiver, betInfo1, BetStatus.COMPLETED);
        bet1.updateBetResult(BetResult.BETTOR_WIN);
        Bet bet2 = createBetAndSave(bettor, receiver, betInfo2, BetStatus.ONGOING);
        Bet bet3 = createBetAndSave(receiver, bettor, betInfo3, BetStatus.COMPLETED);
        bet3.updateBetResult(BetResult.RECEIVER_WIN);
        em.flush();
        em.clear();
        // when
        BetSummaryDto bettingSummary = betRepository.getUserBettingSummary(bettor);
        // then
        UserInfoDto userInfoDto = bettingSummary.getUserInfoDto();
        assertAll(
                () -> assertEquals(userInfoDto.getName(), bettor.getName()),
                () -> assertEquals(userInfoDto.getEmail(), bettor.getEmail()),
                () -> assertEquals(userInfoDto.getProfileImg(), bettor.getProfileImg())
        );
        assertThat(bettingSummary.getTotalBetCount()).isEqualTo(2);
        assertThat(bettingSummary.getTotalBetAmount()).isEqualTo(10000);
    }

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
                .profileImg(name+"img")
                .name(name)
                .build();
        return userRepository.save(user);
    }
}