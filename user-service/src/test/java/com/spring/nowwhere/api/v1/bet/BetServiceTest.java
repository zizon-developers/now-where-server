package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.RequestBet;
import com.spring.nowwhere.api.v1.entity.bet.dto.ResponseBet;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
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

@SpringBootTest
class BetServiceTest {

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
         User bettor = User.builder()
                 .email("bettor@test.com")
                 .checkId("bettorId")
                 .name("bettor")
                 .build();

         User receiver = User.builder()
                 .email("receiver@test.com")
                 .checkId("receiverId")
                 .name("receiver")
                 .build();
         userRepository.saveAll(List.of(bettor,receiver));
         Location location = new Location(454, 589);

         Bet bet1 = Bet.builder()
                 .bettor(bettor)
                 .receiver(receiver)
                 .betStatus(BetStatus.PENDING)
                 .betInfo(BetInfo.builder()
                         .amount(4500)
                         .startTime(LocalDateTime.of(2021, 2, 5, 23, 50))
                         .endTime(LocalDateTime.of(2021, 2, 5, 23, 59))
                         .appointmentLocation(location)
                         .build())
                 .build();

         Bet bet2 = Bet.builder()
                 .bettor(bettor)
                 .receiver(receiver)
                 .betStatus(BetStatus.PENDING)
                 .betInfo(BetInfo.builder()
                         .amount(4500)
                         .startTime(LocalDateTime.of(2021, 2, 6, 00, 10))
                         .endTime(LocalDateTime.of(2021, 2, 6, 00, 59))
                         .appointmentLocation(location)
                         .build())
                 .build();
         betRepository.saveAll(List.of(bet1, bet2));

         return List.of(
                 DynamicTest.dynamicTest("사용자는 다른 사용자에게 내기를 신청할 수 있다.", () -> {
                     //given
                     BetInfo requestBetInfo = BetInfo.builder()
                             .amount(4500)
                             .startTime(LocalDateTime.of(2021, 2, 6, 00, 00))
                             .endTime(LocalDateTime.of(2021, 2, 6, 00, 9))
                             .appointmentLocation(location)
                             .build();

                     RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
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
                     BetInfo betInfo = BetInfo.builder()
                             .amount(4500)
                             .startTime(LocalDateTime.of(2021, 2, 3, 1, 0))
                             .endTime(LocalDateTime.of(2021, 2, 3, 1, 4))
                             .appointmentLocation(location)
                             .build();

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
                     BetInfo requestBetInfo = BetInfo.builder()
                             .amount(4500)
                             .startTime(LocalDateTime.of(2021, 2, 5, 23, 59))
                             .endTime(LocalDateTime.of(2021, 2, 6, 00, 9))
                             .appointmentLocation(location)
                             .build();

                     RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                             .betInfo(requestBetInfo)
                             .build();

                     assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                             .isInstanceOf(TimeValidationException.class)
                             .hasMessage("이미 시간에 포함된 내기가 있습니다.");
                 })
         );
     }
}