package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.bet.dto.UserInfoDto;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
    @DisplayName("특정 시작시간인 내기를 모두 조회할 수 있다.")
    public void findBetsByStartTime() {
        // given
        User bettor = createUserAndSave("bettor1");
        User receiver = createUserAndSave("receiver1");

        int amount = 4500;
        Location location = new Location(454, 589);
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 2, 3);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 1, 2, 3);

        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime,location);
        createBetAndSave(bettor, receiver, betInfo, BetStatus.REQUESTED);

        BetDateTime betDateTime2 = new BetDateTime(startTime, endTime.plusDays(1));
        BetInfo betInfo2 = createBetInfo(amount, betDateTime2,location);
        createBetAndSave(bettor, receiver, betInfo2, BetStatus.REQUESTED);

        BetDateTime betDateTime3 = new BetDateTime(startTime.plusDays(1), endTime.plusDays(2));
        BetInfo betInfo3 = createBetInfo(amount, betDateTime3,location);
        createBetAndSave(bettor, receiver, betInfo3, BetStatus.REQUESTED);
        //when
        List<Bet> finds = betRepository.findBetsByStartTime(startTime);
        //then
        assertThat(finds).hasSize(2);
    }

    @Test
    @DisplayName("특정 종료시간인 내기를 모두 조회할 수 있다.")
    public void findBetsByEndTime() {
        // given
        User bettor = createUserAndSave("bettor1");
        User receiver = createUserAndSave("receiver1");

        int amount = 4500;
        Location location = new Location(454, 589);
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 2, 3);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 1, 2, 3);

        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime,location);
        createBetAndSave(bettor, receiver, betInfo, BetStatus.REQUESTED);

        BetDateTime betDateTime2 = new BetDateTime(startTime.plusDays(2), endTime);
        BetInfo betInfo2 = createBetInfo(amount, betDateTime2,location);
        createBetAndSave(bettor, receiver, betInfo2, BetStatus.REQUESTED);

        BetDateTime betDateTime3 = new BetDateTime(startTime.plusDays(1), endTime.plusDays(2));
        BetInfo betInfo3 = createBetInfo(amount, betDateTime3,location);
        createBetAndSave(bettor, receiver, betInfo3, BetStatus.REQUESTED);
        //when
        List<Bet> finds = betRepository.findBetsByEndTime(endTime);
        //then
        assertThat(finds).hasSize(2);
    }
    @Test
    @DisplayName("사용자 두명의 내기를 저장할 수 있다")
    public void save() {
        // given
        User bettor = createUserAndSave("bettor1");
        User receiver = createUserAndSave("receiver1");

        int amount = 4500;
        Location location = new Location(454, 589);
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 2, 3);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 1, 2, 3);
        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime,location);
        //when
        Bet saved = createBetAndSave(bettor, receiver, betInfo, BetStatus.REQUESTED);
        // then
        assertAll(
                () -> assertEquals(saved.getBettor(), bettor),
                () -> assertEquals(saved.getReceiver(), receiver),
                () -> assertEquals(saved.getBetStatus(), BetStatus.REQUESTED),

                () -> assertEquals(saved.getBetInfo().getAmount(), amount),
                () -> assertEquals(saved.getBetInfo().getBetDateTime().getStartTime(), startTime),
                () -> assertEquals(saved.getBetInfo().getBetDateTime().getEndTime(), endTime),
                () -> assertEquals(saved.getBetInfo().getAppointmentLocation(), location)
        );
    }


    @DisplayName("시작시간과 종료시간에 포함되는 내기 목록을 조회할 수 있다.")
    @TestFactory
    public Collection<DynamicTest> findBetsInTimeRange() {
        // given
        User bettor = createUserAndSave("bettor2");
        User receiver = createUserAndSave("receiver2");
        User test = createUserAndSave("test1");
        Location location = new Location(454, 589);

        LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime1 = new BetDateTime(startTime1, endTime1);
        int amount = 4500;
        BetInfo betInfo1 = createBetInfo(amount, betDateTime1, location);

        LocalDateTime startTime2 = LocalDateTime.of(2021, 2, 6, 00, 20);
        LocalDateTime endTime2 = LocalDateTime.of(2021, 2, 6, 00, 40);
        BetDateTime betDateTime2 = new BetDateTime(startTime2, endTime2);
        BetInfo betInfo2 = createBetInfo(amount, betDateTime2, location);

        createBetAndSave(bettor, test, betInfo1, BetStatus.WAITING);
        createBetAndSave(receiver, test, betInfo2, BetStatus.IN_PROGRESS);

        return List.of(
                DynamicTest.dynamicTest("bettor의 시작시간과 종료시간에 포함되는 내기 목록을 조회할 수 있다.", () -> {
                    // when
                    LocalDateTime bettorStartTime = LocalDateTime.of(2021, 2, 5, 23, 40);
                    LocalDateTime bettorEndTime = LocalDateTime.of(2021, 2, 5, 23, 50);
                    BetDateTime betDateTime = new BetDateTime(bettorStartTime, bettorEndTime);
                    Optional<Bet> bettorBet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime);
                    // then
                    assertThat(bettorBet.isPresent()).isTrue();
                }),
                DynamicTest.dynamicTest("receiver의 시작시간과 종료시간에 포함되는 내기 목록을 조회할 수 있다.", () -> {
                    // when
                    LocalDateTime receiverStartTime = LocalDateTime.of(2021, 2, 6, 00, 40);
                    LocalDateTime receiverEndTime = LocalDateTime.of(2021, 2, 6, 00, 59);
                    BetDateTime betDateTime = new BetDateTime(receiverStartTime, receiverEndTime);
                    Optional<Bet> receiverBet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime);
                    // then

                    assertThat(receiverBet.isPresent()).isTrue();
                })

        );
    }

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("사용자가 진행한 내기 횟수와, 내기로 번 금액을 얻을 수 있다.")
    public void getUserBettingSummary() {
        // given
        User bettor = createUserAndSave("bettor3");
        User receiver = createUserAndSave("receiver3");

        Location location = new Location(454, 589);
        LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime1 = new BetDateTime(startTime1, endTime1);
        BetDateTime betDateTime2 = new BetDateTime(startTime1.plusDays(1), endTime1.plusDays(1));
        BetDateTime betDateTime3 = new BetDateTime(startTime1.plusDays(2), endTime1.plusDays(2));

        BetInfo betInfo1 = createBetInfo(4000, betDateTime1, location);
        BetInfo betInfo2 = createBetInfo(5000, betDateTime2, location);
        BetInfo betInfo3 = createBetInfo(6000, betDateTime3, location);

        Bet bet1 = createBetAndSave(bettor, receiver, betInfo1, BetStatus.COMPLETED);
        bet1.updateBetResult(BetResult.BETTOR_WIN);
        createBetAndSave(bettor, receiver, betInfo2, BetStatus.IN_PROGRESS);
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

    private BetInfo createBetInfo(int amount, BetDateTime betDateTime, Location location) {
        return BetInfo.builder()
                .amount(amount)
                .betDateTime(betDateTime)
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