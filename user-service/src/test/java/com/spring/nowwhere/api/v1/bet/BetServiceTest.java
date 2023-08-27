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

         return List.of(
                 DynamicTest.dynamicTest("사용자는 다른 사용자에게 내기를 신청할 수 있다.", () -> {
                     //given
                     Location location = new Location(454, 589);
                     BetInfo betInfo = BetInfo.builder()
                             .amount(4500)
                             .startTime(LocalDateTime.of(2021, 2, 3, 1, 2, 3))
                             .endTime(LocalDateTime.of(2021, 2, 5, 1, 2, 4))
                             .appointmentLocation(location)
                             .build();

                     RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                                             .betInfo(betInfo)
                                             .build();
                     //when
                     ResponseBet responseBet = betService.createBet(bettor.getCheckId(), requestBet);
                     //then
                     assertAll(
                             () -> assertEquals(responseBet.getBettorId(),bettor.getCheckId()),
                             () -> assertEquals(responseBet.getReceiverId(),requestBet.getReceiverId()),
                             () -> assertEquals(responseBet.getBetInfo(),betInfo),
                             () -> assertEquals(responseBet.getBetInfo().getAppointmentLocation(),location),
                             () -> assertEquals(responseBet.getBetStatus(),BetStatus.PENDING)
                     );

                 }),
                 DynamicTest.dynamicTest("예외", () -> {
                     //given

                     //when

                     //then

                 })
         );
     }
}