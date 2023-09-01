package com.spring.nowwhere.api.v1.entity.friend.service;

import com.spring.nowwhere.api.v1.entity.friend.*;
import com.spring.nowwhere.api.v1.entity.friend.exception.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendNotFoundException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FriendServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private FriendService friendService;

    @AfterEach
    void tearDown() {
        friendRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 친구요청 시나리오")
    @TestFactory
    Collection<DynamicTest> createFriendRequest() {
        // given
        User sender = createAndSaveUser("sender");
        return List.of(
                DynamicTest.dynamicTest("1.사용자는 다른 사용자들에게 친구추가를 요청할 수 있다.", () -> {
                    //given
                    User receiver = createAndSaveUser("receiver1");
                    //when
                    friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
                    //then
                    Friend friend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
                    assertAll(
                            //역방향 레코드 조회 주의하기
                            () -> assertEquals(friend.getSender(), sender),
                            () -> assertEquals(friend.getReceiver(), receiver),
                            () -> assertEquals(friend.getFriendStatus(), FriendStatus.PENDING)
                    );
                }),
                DynamicTest.dynamicTest("2.사용자는 다른 사용자들에게 친구추가를 요청할 수 있다.", () -> {
                    //given
                    User receiver = createAndSaveUser("receiver2");
                    //when
                    friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
                    //then
                    Friend friend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
                    assertAll(
                            //역방향 레코드 조회 주의하기
                            () -> assertEquals(friend.getSender(), sender),
                            () -> assertEquals(friend.getReceiver(), receiver),
                            () -> assertEquals(friend.getFriendStatus(), FriendStatus.PENDING)
                    );
                }),
                DynamicTest.dynamicTest("3.사용자는 다른 사용자들에게 친구추가를 요청할 수 있다.", () -> {
                    //given
                    User receiver = createAndSaveUser("receiver3");
                    //when
                    friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
                    //then
                    Friend friend2 = friendRepository.findBySenderAndReceiver(sender, receiver).get();
                    assertAll(
                            //역방향 레코드 조회 주의하기
                            () -> assertEquals(friend2.getSender(), sender),
                            () -> assertEquals(friend2.getReceiver(), receiver),
                            () -> assertEquals(friend2.getFriendStatus(), FriendStatus.PENDING)
                    );
                })
        );
    }

    @DisplayName("사용자 친구요청 예외 시나리오")
    @TestFactory
    Collection<DynamicTest> createFriendRequest_EX() {
        // given
        return List.of(
                DynamicTest.dynamicTest("요청 대기상태에서 친구 요청시 예외가 발생한다.", () -> {
                    //given
                    User sender = createAndSaveUser("sender1");
                    User receiver = createAndSaveUser("receiver1");

                    createAndSaveFriend(sender, receiver, FriendStatus.PENDING);
                    //when //then
                    assertThatThrownBy(() -> friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendRequestPendingException.class)
                            .hasMessage("요청 대기상태 입니다.");
                }),
                DynamicTest.dynamicTest("이미 친구인 상태에서 친구 요청시 예외가 발생한다.", () -> {
                    //given
                    User sender = createAndSaveUser("sender2");
                    User receiver = createAndSaveUser("receiver2");

                    createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);
                    //when //then
                    assertThatThrownBy(() -> friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(AlreadyFriendsException.class)
                            .hasMessage("이미 친구상태 입니다.");
                })
        );
    }


    @Test
    @DisplayName("사용자는 친구 요청을 수락 수 있다.")
    public void updateFriendRequestToAccept() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
        // when
        friendService.updateFriendRequestToAccept(sender.getCheckId(), receiver.getCheckId());
        // then
        Friend friend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
        assertAll(
                () -> assertEquals(friend.getSender(), sender),
                () -> assertEquals(friend.getReceiver(), receiver),
                () -> assertEquals(friend.getFriendStatus(), FriendStatus.COMPLETED)
        );
    }

    @DisplayName("친구 요청 상태가 대기 상태가 아닐경우 친구 요청을 수락할 때 예외가 발생한다.")
    @TestFactory
    Collection<DynamicTest> updateFriendRequestToAccept_EX() {
        return List.of(
                DynamicTest.dynamicTest("요청 상태가 COMPLETED인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender1");
                    User receiver = createAndSaveUser("receiver1");
                    createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToAccept(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 CANCELED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender2");
                    User receiver = createAndSaveUser("receiver2");
                    createAndSaveFriend(sender, receiver, FriendStatus.CANCELED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToAccept(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 DENIED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender3");
                    User receiver = createAndSaveUser("receiver3");
                    createAndSaveFriend(sender, receiver, FriendStatus.DENIED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToAccept(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                })
        );
    }

    @Test
    @DisplayName("사용자는 친구 요청을 거절할 수 있다.")
    public void updateFriendRequestToReject() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
        // when
        friendService.updateFriendRequestToReject(sender.getCheckId(), receiver.getCheckId());
        // then
        Friend friend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
        assertAll(
                () -> assertEquals(friend.getSender(), sender),
                () -> assertEquals(friend.getReceiver(), receiver),
                () -> assertEquals(friend.getFriendStatus(), FriendStatus.DENIED_REQUEST)
        );
    }

    @DisplayName("친구 요청 상태가 대기 상태가 아닐경우 친구 요청을 거절할 때 예외가 발생한다.")
    @TestFactory
    Collection<DynamicTest> updateFriendRequestToReject_EX() {
        return List.of(
                DynamicTest.dynamicTest("요청 상태가 COMPLETED인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender1");
                    User receiver = createAndSaveUser("receiver1");
                    createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToReject(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 CANCELED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender2");
                    User receiver = createAndSaveUser("receiver2");
                    createAndSaveFriend(sender, receiver, FriendStatus.CANCELED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToReject(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 DENIED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender3");
                    User receiver = createAndSaveUser("receiver3");
                    createAndSaveFriend(sender, receiver, FriendStatus.DENIED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToReject(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                })
        );
    }

    @Test
    @DisplayName("사용자는 보낸 친구요청을 취소할 수 있다.")
    public void updateFriendRequestToCancel() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId());
        // when
        friendService.updateFriendRequestToCancel(sender.getCheckId(), receiver.getCheckId());
        // then
        Friend friend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
        assertAll(
                () -> assertEquals(friend.getSender(), sender),
                () -> assertEquals(friend.getReceiver(), receiver),
                () -> assertEquals(friend.getFriendStatus(), FriendStatus.CANCELED_REQUEST)
        );
    }

    @DisplayName("친구 요청 상태가 대기 상태가 아닐경우 친구 요청을 취소할 때 예외가 발생한다.")
    @TestFactory
    Collection<DynamicTest> updateFriendRequestToCancel_EX() {
        return List.of(
                DynamicTest.dynamicTest("요청 상태가 COMPLETED인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender1");
                    User receiver = createAndSaveUser("receiver1");
                    createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToCancel(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 CANCELED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender2");
                    User receiver = createAndSaveUser("receiver2");
                    createAndSaveFriend(sender, receiver, FriendStatus.CANCELED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToCancel(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                }),
                DynamicTest.dynamicTest("요청 상태가 DENIED_REQUEST인 경우 예외가 발생한다.", () -> {
                    // given
                    User sender = createAndSaveUser("sender3");
                    User receiver = createAndSaveUser("receiver3");
                    createAndSaveFriend(sender, receiver, FriendStatus.DENIED_REQUEST);

                    //when //then
                    assertThatThrownBy(() -> friendService.updateFriendRequestToCancel(sender.getCheckId(), receiver.getCheckId()))
                            .isInstanceOf(FriendNotFoundException.class)
                            .hasMessage("친구 요청 정보가 존재하지 않습니다.");
                })
        );
    }
//    @Test
    @DisplayName("사용자는 특정 친구를 삭제할 수 있다.")
    public void removeFriend() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);
        createAndSaveFriend(receiver, sender, FriendStatus.COMPLETED);
        // when
        friendService.removeFriend(sender.getCheckId(),receiver.getCheckId());
        // then
    }

    private User createAndSaveUser(String name) {
        User user = User.builder()
                .email(name + "@test.com")
                .name(name)
                .checkId(name + "Id")
                .build();

        return userRepository.save(user);
    }

    private Friend createAndSaveFriend(User sender, User receiver, FriendStatus status) {
        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(status)
                .build();

        return friendRepository.save(friend);
    }
}