package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.RequestBet;
import com.spring.nowwhere.api.v1.entity.bet.dto.ResponseBet;
import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.bet.service.BetService;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BetServiceTest extends IntegrationTestSupport {

    @Autowired
    private BetService betService;
    @Autowired
    private BetRepository betRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown(){
        betRepository.deleteAllInBatch();
    }

     @DisplayName("사용자 내기 시나리오")
     @TestFactory
     Collection<DynamicTest> createBet() {
         // given
         User bettor = createUserAndSave("bettor");
         User receiver = createUserAndSave("receiver");
         int amount = 4500;
         Location location = new Location(454, 589);


         LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 50);
         LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 59);
         BetInfo betInfo1 = createBetInfo(amount, startTime1, endTime1, location);
         createBetAndSave(bettor, receiver, betInfo1, BetStatus.PENDING);

         LocalDateTime startTime2 = LocalDateTime.of(2021, 2, 6, 00, 10);
         LocalDateTime endTime2 = LocalDateTime.of(2021, 2, 6, 00, 59);
         BetInfo betInfo2 = createBetInfo(amount, startTime2, endTime2, location);
         createBetAndSave(bettor, receiver, betInfo2, BetStatus.PENDING);

         return List.of(
                 DynamicTest.dynamicTest("사용자는 다른 사용자에게 내기를 신청할 수 있다.", () -> {
                     //given
                     LocalDateTime startTime = LocalDateTime.of(2021, 2, 6, 00, 00);
                     LocalDateTime endTime = LocalDateTime.of(2021, 2, 6, 00, 9);
                     BetInfo requestBetInfo = createBetInfo(amount, startTime, endTime, location);

                     RequestBet requestBet = RequestBet.builder()
                                                         .receiverId("receiverId")
                                                         .betInfo(requestBetInfo)
                                                         .build();
                     //when
                     ResponseBet responseBet = betService.createBet(bettor.getCheckId(), requestBet);
                     //then
                     assertAll(
                             () -> assertEquals(responseBet.getBettorId(),bettor.getCheckId()),
                             () -> assertEquals(responseBet.getReceiverId(),requestBet.getReceiverId()),
                             () -> assertEquals(responseBet.getBetInfo(),requestBetInfo),
                             () -> assertEquals(responseBet.getBetInfo().getAppointmentLocation(),location),
                             () -> assertEquals(responseBet.getBetStatus(),BetStatus.PENDING)
                     );

                 }),
                 DynamicTest.dynamicTest("내기의 시작시간과 끝나는 시간의 차이가 5분 이하라면 예외가 발생한다.", () -> {
                     //given
                     LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 0);
                     LocalDateTime endTime = LocalDateTime.of(2021, 2, 3, 1, 4);
                     BetInfo betInfo = createBetInfo(amount, startTime, endTime, location);

                     RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                                                                 .betInfo(betInfo)
                                                                 .build();
                     //when //then
                     assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                             .isInstanceOf(TimeValidationException.class)
                             .hasMessage("내기의 시작시간과 끝나는 시간의 차이는 5분 이상이어야 합니다.");

                 }),
                 DynamicTest.dynamicTest("이미 지정된 시간에 다른 내기가 있다면 예외가 발생한다.", () -> {
                     //when //then
                     LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 59);
                     LocalDateTime endTime = LocalDateTime.of(2021, 2, 6, 00, 9);
                     BetInfo requestBetInfo = createBetInfo(amount, startTime, endTime, location);
                     RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                             .betInfo(requestBetInfo)
                             .build();

                     assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                             .isInstanceOf(TimeValidationException.class)
                             .hasMessage("이미 시간에 포함된 내기가 있습니다.");
                 })
         );
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
                .checkId(name+"Id")
                .email(name+"@test.com")
                .name(name)
                .build();
        return userRepository.save(user);
    }
}