package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FriendRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;

    @AfterEach
    void tearDown() {
        friendRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("두 사용자의 친구 정보를 조회할 수 있다.")
    public void areFriends() {
        // given
        User sender = createAndSaveUser("sender1");
        User receiver = createAndSaveUser("receiver2");

        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);
        // when
        Friend findFriend = friendRepository.areFriends(sender, receiver).get();
        // then
        assertAll(
                () -> assertEquals(findFriend.getFriendStatus(), FriendStatus.PENDING),
                () -> assertEquals(findFriend.getReceiver(), receiver),
                () -> assertEquals(findFriend.getSender(), sender)
        );
    }

     @DisplayName("친구 요청을 받은 사용자가 특정 상태에서 친구 요청 받은 정보를 조회할 수 있다.")
     @TestFactory
     Collection<DynamicTest> getReceiversWithStatus () {
        // given
         User sender1 = createAndSaveUser("sender1");
         User sender2 = createAndSaveUser("sender2");
         User sender3 = createAndSaveUser("sender3");
         User sender4 = createAndSaveUser("sender4");
         User receiver = createAndSaveUser("receiver");

         Friend friend1 = Friend.builder()
                 .sender(sender1)
                 .receiver(receiver)
                 .friendStatus(FriendStatus.PENDING)
                 .build();

         Friend friend2 = Friend.builder()
                 .sender(sender2)
                 .receiver(receiver)
                 .friendStatus(FriendStatus.DENIED_REQUEST)
                 .build();

         Friend friend3 = Friend.builder()
                 .sender(sender3)
                 .receiver(receiver)
                 .friendStatus(FriendStatus.CANCELED_REQUEST)
                 .build();

         Friend friend4 = Friend.builder()
                 .sender(sender4)
                 .receiver(receiver)
                 .friendStatus(FriendStatus.COMPLETED)
                 .build();

         friendRepository.saveAll(List.of(friend1,friend2,friend3,friend4));
        return List.of(
                DynamicTest.dynamicTest("친구 요청이 PENDING 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getReceiversWithStatus(receiver, FriendStatus.PENDING).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getReceiver(),receiver),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.PENDING)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 DENIED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getReceiversWithStatus(receiver, FriendStatus.DENIED_REQUEST).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getReceiver(),receiver),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.DENIED_REQUEST)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 CANCELED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getReceiversWithStatus(receiver, FriendStatus.CANCELED_REQUEST).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getReceiver(),receiver),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.CANCELED_REQUEST)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 COMPLETED 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getReceiversWithStatus(receiver, FriendStatus.COMPLETED).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getReceiver(),receiver),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.COMPLETED)
                    );
                })
        );
    }
    @DisplayName("친구 요청을 보낸 사용자가 특정 상태에서 친구 요청 받은 정보를 조회할 수 있다.")
    @TestFactory
    Collection<DynamicTest> getSendersWithStatus () {
        // given
        User sender = createAndSaveUser("sender");
        User receiver1 = createAndSaveUser("receiver1");
        User receiver2 = createAndSaveUser("receiver2");
        User receiver3 = createAndSaveUser("receiver3");
        User receiver4 = createAndSaveUser("receiver4");

        Friend friend1 = Friend.builder()
                .sender(sender)
                .receiver(receiver1)
                .friendStatus(FriendStatus.PENDING)
                .build();

        Friend friend2 = Friend.builder()
                .sender(sender)
                .receiver(receiver2)
                .friendStatus(FriendStatus.DENIED_REQUEST)
                .build();

        Friend friend3 = Friend.builder()
                .sender(sender)
                .receiver(receiver3)
                .friendStatus(FriendStatus.CANCELED_REQUEST)
                .build();

        Friend friend4 = Friend.builder()
                .sender(sender)
                .receiver(receiver4)
                .friendStatus(FriendStatus.COMPLETED)
                .build();

        friendRepository.saveAll(List.of(friend1,friend2,friend3,friend4));
        return List.of(
                DynamicTest.dynamicTest("친구 요청이 PENDING 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getSendersWithStatus(sender, FriendStatus.PENDING).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getSender(),sender),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.PENDING)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 DENIED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getSendersWithStatus(sender, FriendStatus.DENIED_REQUEST).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getSender(),sender),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.DENIED_REQUEST)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 CANCELED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getSendersWithStatus(sender, FriendStatus.CANCELED_REQUEST).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getSender(),sender),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.CANCELED_REQUEST)
                    );
                }),
                DynamicTest.dynamicTest("친구 요청이 COMPLETED 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Friend friend = friendRepository.getSendersWithStatus(sender, FriendStatus.COMPLETED).get();
                    //then
                    Assertions.assertAll(
                            () -> assertEquals(friend.getSender(),sender),
                            () -> assertEquals(friend.getFriendStatus(),FriendStatus.COMPLETED)
                    );
                })
        );
    }

    private User createAndSaveUser(String name) {
        User user = User.builder()
                .email(name + "@test.com")
                .name(name)
                .checkId(name + "id")
                .build();
        return userRepository.save(user);
    }

}