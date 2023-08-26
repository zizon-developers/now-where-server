//package com.spring.nowwhere.api.v1.bet;
//
//import com.spring.nowwhere.api.v1.user.entity.User;
//import com.spring.nowwhere.api.v1.user.repository.UserRepository;
//import org.checkerframework.checker.units.qual.A;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.DynamicTest;
//import org.junit.jupiter.api.TestFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Collection;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class BetServiceTest {
//
////    @Autowired
////    private BetService betService;
//    @Autowired
//    private BetRepository betRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @AfterEach
//    void tearDown(){
//        betRepository.deleteAllInBatch();
//    }
//
//     @DisplayName("사용자 내기 시나리오")
//     @TestFactory
//     Collection<DynamicTest> createBet() {
//         // given
//         User bettor = User.builder()
//                 .email("bettor@test.com")
//                 .userId("bettorId")
//                 .name("bettor")
//                 .build();
//
//         User receiver = User.builder()
//                 .email("receiver@test.com")
//                 .userId("receiverId")
//                 .name("receiver")
//                 .build();
//         userRepository.saveAll(List.of(bettor,receiver));
//
//         return List.of(
//                 DynamicTest.dynamicTest("사용자는 다른 사용자에게 내기를 신청할 수 있다.", () -> {
//                     //given
//                     RequestBet requestBet = new RequestBet(receiver.getUserId(), 4500);
//                     //when
//                     betService.createBet(bettor.getUserId(), requestBet);
//                     //then
//
//                 }),
//                 DynamicTest.dynamicTest("예외", () -> {
//                     //given
//
//                     //when
//
//                     //then
//
//                 })
//         );
//     }
//}