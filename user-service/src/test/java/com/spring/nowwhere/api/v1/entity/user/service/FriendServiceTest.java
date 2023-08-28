package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.entity.user.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.user.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.user.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.entity.Friend;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;

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
    Collection<DynamicTest> sendFriendRequest() {
        // given
        return List.of(
                DynamicTest.dynamicTest("사용자는 다른 사용자에게 친구추가를 요청할 수 있다.", () -> {
                    //given
                    List<User> users = createUser("sender", "receiver");
                    User sender = users.get(0);
                    User receiver = users.get(1);

                    //when
                    friendService.sendFriendRequest(sender,receiver);
                    //then
                    Friend friend = friendRepository.areFriends(sender, receiver).get();
                    assertAll(
                            //역방향 레코드 조회 주의하기
                            () -> assertEquals(friend.getSender(),receiver),
                            () -> assertEquals(friend.getReceiver(),sender),
                            () -> assertEquals(friend.getFriendStatus(), FriendStatus.PENDING)
                    );

                }),
                DynamicTest.dynamicTest("친구 요청 ", () -> {
                    //given
                    List<User> users = createUser("sender1", "receiver1");
                    User sender = users.get(0);
                    User receiver = users.get(1);

                    Friend fromUserFriend = Friend.builder()
                            .sender(sender)
                            .receiver(receiver)
                            .friendStatus(FriendStatus.COMPLETED)
                            .build();

                    Friend toUserFriend = Friend.builder()
                            .sender(receiver)
                            .receiver(sender)
                            .friendStatus(FriendStatus.PENDING)
                            .build();

                    friendRepository.saveAll(List.of(fromUserFriend, toUserFriend));
                    //when //then
                    Assertions.assertThatThrownBy(() -> friendService.sendFriendRequest(sender, receiver))
                            .isInstanceOf(FriendRequestPendingException.class)
                            .hasMessage("아직 요청 대기상태 입니다.");
                }),
                DynamicTest.dynamicTest("예외", () -> {
                    //given
                    List<User> users = createUser("sender2", "receiver2");
                    User sender = users.get(0);
                    User receiver = users.get(1);

                    Friend fromUserFriend = Friend.builder()
                            .sender(sender)
                            .receiver(receiver)
                            .friendStatus(FriendStatus.COMPLETED)
                            .build();

                    Friend toUserFriend = Friend.builder()
                            .sender(receiver)
                            .receiver(sender)
                            .friendStatus(FriendStatus.COMPLETED)
                            .build();

                    friendRepository.saveAll(List.of(fromUserFriend, toUserFriend));
                    //when //then
                    Assertions.assertThatThrownBy(() -> friendService.sendFriendRequest(sender, receiver))
                            .isInstanceOf(AlreadyFriendsException.class)
                            .hasMessage("이미 친구상태 입니다.");
                })
        );
    }
    private List<User> createUser(String senderName, String receiverName){
        User sender = User.builder()
                .email(senderName+"@test.com")
                .name(senderName)
                .checkId(senderName+"Id")
                .build();

        User receiver = User.builder()
                .email(receiverName+"@test.com")
                .name(receiverName)
                .checkId(receiverName+"id")
                .build();
        userRepository.saveAll(List.of(sender, receiver));
        return List.of(sender, receiver);
    }
}