package com.spring.nowwhere.api.v1.bet;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.*;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetStatusException;
import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.bet.service.BetService;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.spring.nowwhere.api.v1.entity.bet.BetStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BetServiceTest extends IntegrationTestSupport {

    @Autowired
    private BetService betService;
    @Autowired
    private BetRepository betRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        betRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("사용자는 내기를 수락할 수 있다.")
    public void acceptBet() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");

        int amount = 4500;
        Location location = new Location(454, 589,"목적지");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime, location);
        createBetAndSave(bettor, receiver, betInfo, REQUESTED);

        // when
        AcceptBetRequest request = createAcceptBetRequest(bettor, betDateTime);
        betService.acceptBet(receiver.getCheckId(),request);
        Bet findBet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime).get();
        // then
        assertThat(findBet.getBetStatus()).isEqualTo(WAITING);
    }

    private static AcceptBetRequest createAcceptBetRequest(User bettor, BetDateTime betDateTime) {
        return AcceptBetRequest.builder()
                .bettorId(bettor.getCheckId())
                .betDateTime(betDateTime)
                .build();
    }

    @TestFactory
    @Transactional
    @DisplayName("사용자의 내기가 요청상태가 아닌경우 예외가 발생한다.")
    public Collection<DynamicTest> acceptBet_EX() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        int amount = 4500;
        Location location = new Location(454, 589,"목적지");

        return List.of(
                DynamicTest.dynamicTest("내기가 대기중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, WAITING);
                    // when // then
                    AcceptBetRequest request = createAcceptBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.acceptBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기가 진행중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(1), endTime.plusDays(1));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, IN_PROGRESS);
                    // when // then
                    AcceptBetRequest request = createAcceptBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.acceptBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기가 완료되었을 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(2), endTime.plusDays(2));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, COMPLETED);
                    // when // then
                    AcceptBetRequest request = createAcceptBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.acceptBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기 정보가 없는 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(3), endTime.plusDays(3));
                    // when // then
                    AcceptBetRequest request = createAcceptBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.acceptBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetNotFoundException.class)
                            .hasMessage("해당하는 내기 정보가 없습니다.");
                })
        );
    }

    @Test
    @DisplayName("사용자는 내기를 거절할 수 있다.")
    public void rejectBet() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");

        int amount = 4500;
        Location location = new Location(454, 589,"목적지");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime, location);
        createBetAndSave(bettor, receiver, betInfo, REQUESTED);

        // when
        RejectBetRequest request = createRejectBetRequest(bettor, betDateTime);
        betService.rejectBet(receiver.getCheckId(),request);
        Optional<Bet> findBet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime);
        // then
        assertThat(findBet.isEmpty()).isTrue();
    }

    private static RejectBetRequest createRejectBetRequest(User bettor, BetDateTime betDateTime) {
        return RejectBetRequest.builder()
                .bettorId(bettor.getCheckId())
                .betDateTime(betDateTime)
                .build();
    }

    @TestFactory
    @Transactional
    @DisplayName("사용자의 내기가 요청상태가 아닌경우 예외가 발생한다.")
    public Collection<DynamicTest> rejectBet_EX() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        int amount = 4500;
        Location location = new Location(454, 589,"목적지");

        return List.of(
                DynamicTest.dynamicTest("내기가 대기중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, WAITING);
                    // when // then
                    RejectBetRequest request = createRejectBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.rejectBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기가 진행중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(1), endTime.plusDays(1));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, IN_PROGRESS);
                    // when // then
                    RejectBetRequest request = createRejectBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.rejectBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기가 완료되었을 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(2), endTime.plusDays(2));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, COMPLETED);
                    // when // then
                    RejectBetRequest request = createRejectBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.rejectBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("내기의 상태가 요청상태가 아닙니다.");
                }),
                DynamicTest.dynamicTest("내기 정보가 없는 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(3), endTime.plusDays(3));
                    // when // then
                    RejectBetRequest request = createRejectBetRequest(bettor, betDateTime);

                    assertThatThrownBy(
                            () -> betService.rejectBet(receiver.getCheckId(),request))
                            .isInstanceOf(BetNotFoundException.class)
                            .hasMessage("해당하는 내기 정보가 없습니다.");
                })
        );
    }

    @Test
    @DisplayName("사용자는 내기를 취소할 수 있다.")
    public void removeBet() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");

        int amount = 4500;
        Location location = new Location(454, 589,"목적지");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime, location);
        createBetAndSave(bettor, receiver, betInfo, REQUESTED);

        // when
        RemoveBetRequest removeBetRequest = createRemoveBetRequest(receiver, betDateTime);
        betService.removeBet(bettor.getCheckId(),removeBetRequest);
        Optional<Bet> findBet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime);
        // then
        assertThat(findBet.isEmpty()).isTrue();
    }

    private static RemoveBetRequest createRemoveBetRequest(User receiver, BetDateTime betDateTime) {
        return RemoveBetRequest.builder()
                .receiverId(receiver.getCheckId())
                .betDateTime(betDateTime)
                .build();
    }

    @TestFactory
    @Transactional
    @DisplayName("사용자는 내기가 진행중이거나, 완료된 경우 내기 정보를 업데이트 할 경우 예외가 발생한다.")
    public Collection<DynamicTest> removeBet_EX() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        int amount = 4500;
        Location location = new Location(454, 589,"목적지");

        return List.of(
                DynamicTest.dynamicTest("내기가 진행중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, IN_PROGRESS);
                    // when // then
                    RemoveBetRequest removeBetRequest = createRemoveBetRequest(receiver, betDateTime);

                    assertThatThrownBy(
                            () -> betService.removeBet(bettor.getCheckId(),removeBetRequest))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("이미 내기가 진행중이거나, 완료되었습니다.");
                }),
                DynamicTest.dynamicTest("내기가 완료되었을 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(1), endTime.plusDays(1));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, COMPLETED);
                    // when // then
                    RemoveBetRequest removeBetRequest = createRemoveBetRequest(receiver, betDateTime);

                    assertThatThrownBy(
                            () -> betService.removeBet(bettor.getCheckId(),removeBetRequest))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("이미 내기가 진행중이거나, 완료되었습니다.");
                }),
                DynamicTest.dynamicTest("내기 정보가 없는 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(2), endTime.plusDays(2));
                    // when // then
                    RemoveBetRequest removeBetRequest = createRemoveBetRequest(receiver, betDateTime);

                    assertThatThrownBy(
                            () -> betService.removeBet(bettor.getCheckId(),removeBetRequest))
                            .isInstanceOf(BetNotFoundException.class)
                            .hasMessage("해당하는 내기 정보가 없습니다.");
                })
        );
    }

    @Test
    @Transactional
    @DisplayName("사용자는 내기정보를 업데이트 할 수 있다.")
    public void updateBetInfo() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");

        int amount = 4500;
        Location location = new Location(454, 589,"목적지");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime, location);
        createBetAndSave(bettor, receiver, betInfo, REQUESTED);
        // when
        int updateAmount = 6000;
        UpdateBetRequest updateBetRequest = UpdateBetRequest.builder()
                .receiverId(receiver.getCheckId())
                .betDateTime(betDateTime)
                .updateBetInfoRequest(new UpdateBetRequest
                        .UpdateInfoRequest(null, updateAmount, null))
                .build();
        betService.updateBetInfo(bettor.getCheckId(), updateBetRequest);
        em.flush();
        em.clear();
        // then
        Bet bet = betRepository.findBetInTimeRange(bettor, receiver, betDateTime).get();
        BetInfo findBetInfo = bet.getBetInfo();
        assertAll(
                () -> assertEquals(findBetInfo.getBetDateTime(), betDateTime),
                () -> assertEquals(findBetInfo.getAmount(), updateAmount),
                () -> assertEquals(findBetInfo.getAppointmentLocation(), location)
        );
    }

    @TestFactory
    @Transactional
    @DisplayName("사용자는 내기가 진행중이거나, 완료된 경우 내기 정보를 업데이트 할 경우 예외가 발생한다.")
    public Collection<DynamicTest> updateBetInfo_EX() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 5, 23, 50);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 23, 59);
        int amount = 4500;
        Location location = new Location(454, 589,"목적지");

        return List.of(
                DynamicTest.dynamicTest("내기가 진행중일 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {

                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, IN_PROGRESS);
                    // when // then
                    int updateAmount = 6000;
                    UpdateBetRequest updateBetRequest = UpdateBetRequest.builder()
                            .receiverId(receiver.getCheckId())
                            .betDateTime(betDateTime)
                            .updateBetInfoRequest(new UpdateBetRequest
                                    .UpdateInfoRequest(null, updateAmount, null))
                            .build();
                    assertThatThrownBy(
                            () -> betService.updateBetInfo(bettor.getCheckId(), updateBetRequest))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("이미 내기가 진행중이거나, 완료되었습니다.");
                }),
                DynamicTest.dynamicTest("내기가 완료되었을 때 내기 정보를 업데이트 할 경우 예외가 발생한다.", () -> {
                    BetDateTime betDateTime = new BetDateTime(startTime.plusDays(1), endTime.plusDays(1));
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);
                    createBetAndSave(bettor, receiver, betInfo, COMPLETED);
                    // when // then
                    int updateAmount = 8000;
                    UpdateBetRequest updateBetRequest = UpdateBetRequest.builder()
                            .receiverId(receiver.getCheckId())
                            .betDateTime(betDateTime)
                            .updateBetInfoRequest(new UpdateBetRequest
                                    .UpdateInfoRequest(null, updateAmount, null))
                            .build();
                    assertThatThrownBy(
                            () -> betService.updateBetInfo(bettor.getCheckId(), updateBetRequest))
                            .isInstanceOf(BetStatusException.class)
                            .hasMessage("이미 내기가 진행중이거나, 완료되었습니다.");
                })
        );
    }

    @DisplayName("사용자 내기 시나리오")
    @TestFactory
    Collection<DynamicTest> createBet() {
        // given
        User bettor = createUserAndSave("bettor");
        User receiver = createUserAndSave("receiver");
        int amount = 4500;
        Location location = new Location(454, 589,"목적지");


        LocalDateTime startTime1 = LocalDateTime.of(2021, 2, 5, 23, 10);
        LocalDateTime endTime1 = LocalDateTime.of(2021, 2, 5, 23, 20);
        BetDateTime betDateTime1 = new BetDateTime(startTime1, endTime1);
        BetInfo betInfo1 = createBetInfo(amount, betDateTime1, location);
        createBetAndSave(bettor, receiver, betInfo1, REQUESTED);

        LocalDateTime startTime2 = LocalDateTime.of(2021, 2, 6, 00, 10);
        LocalDateTime endTime2 = LocalDateTime.of(2021, 2, 6, 00, 20);
        BetDateTime betDateTime2 = new BetDateTime(startTime2, endTime2);
        BetInfo betInfo2 = createBetInfo(amount, betDateTime2, location);
        createBetAndSave(bettor, receiver, betInfo2, REQUESTED);

        return List.of(
                DynamicTest.dynamicTest("사용자는 다른 사용자에게 내기를 신청할 수 있다.", () -> {
                    //given
                    LocalDateTime startTime = LocalDateTime.of(2021, 2, 7, 00, 00);
                    LocalDateTime endTime = LocalDateTime.of(2021, 2, 7, 00, 10);
                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo requestBetInfo = createBetInfo(amount, betDateTime, location);

                    RequestBet requestBet = RequestBet.builder()
                            .receiverId("receiverId")
                            .betInfo(requestBetInfo)
                            .build();
                    //when
                    ResponseBet responseBet = betService.createBet(bettor.getCheckId(), requestBet);
                    //then
                    assertAll(
                            () -> assertEquals(responseBet.getBettorId(), bettor.getCheckId()),
                            () -> assertEquals(responseBet.getReceiverId(), requestBet.getReceiverId()),
                            () -> assertEquals(responseBet.getBetInfo(), requestBetInfo),
                            () -> assertEquals(responseBet.getBetInfo().getAppointmentLocation(), location),
                            () -> assertEquals(responseBet.getBetStatus(), REQUESTED)
                    );

                }),
                DynamicTest.dynamicTest("내기의 시작시간과 끝나는 시간의 차이가 10분 이하라면 예외가 발생한다.", () -> {
                    //given
                    LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 0);
                    LocalDateTime endTime = LocalDateTime.of(2021, 2, 3, 1, 9);
                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo betInfo = createBetInfo(amount, betDateTime, location);

                    RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                            .betInfo(betInfo)
                            .build();
                    //when //then
                    assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                            .isInstanceOf(TimeValidationException.class)
                            .hasMessage("내기의 시작시간과 끝나는 시간의 차이는 10분 이상이어야 합니다.");

                }),
                DynamicTest.dynamicTest("이미 지정된 시간에 다른 내기가 있다면 예외가 발생한다.", () -> {
                    //when //then
                    LocalDateTime startTime = LocalDateTime.of(2021, 2, 6, 00, 00);
                    LocalDateTime endTime = LocalDateTime.of(2021, 2, 6, 00, 20);
                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo requestBetInfo = createBetInfo(amount, betDateTime, location);
                    RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                            .betInfo(requestBetInfo)
                            .build();

                    assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                            .isInstanceOf(TimeValidationException.class)
                            .hasMessage("이미 시간에 포함된 내기가 있습니다.");
                }),
                DynamicTest.dynamicTest("내기를 10분 단위로 설정하지 않으면 예외가 발생한다.", () -> {
                    //when //then
                    LocalDateTime startTime = LocalDateTime.of(2021, 2, 9, 23, 59);
                    LocalDateTime endTime = LocalDateTime.of(2021, 2, 9, 00, 10);
                    BetDateTime betDateTime = new BetDateTime(startTime, endTime);
                    BetInfo requestBetInfo = createBetInfo(amount, betDateTime, location);
                    RequestBet requestBet = RequestBet.builder().receiverId("receiverId")
                            .betInfo(requestBetInfo)
                            .build();

                    assertThatThrownBy(() -> betService.createBet(bettor.getCheckId(), requestBet))
                            .isInstanceOf(TimeValidationException.class)
                            .hasMessage("내기의 시작시간과 끝나는 시간의 차이는 10분 이상이어야 합니다.");
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

    private BetInfo createBetInfo(int amount, BetDateTime betDateTime, Location location) {
        return BetInfo.builder()
                .amount(amount)
                .betDateTime(betDateTime)
                .appointmentLocation(location)
                .build();
    }

    private User createUserAndSave(String name) {
        User user = User.builder()
                .checkId(name + "Id")
                .email(name + "@test.com")
                .name(name)
                .build();
        return userRepository.save(user);
    }
}