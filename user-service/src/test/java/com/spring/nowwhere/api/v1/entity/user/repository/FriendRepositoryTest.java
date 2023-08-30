package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        User sender = createUser("sender1");
        User receiver = createUser("receiver2");

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

     @DisplayName("사용자가 특정 상태에서 친구 요청 받은 정보를 조회할 수 있다.")
     @TestFactory
     Collection<DynamicTest> getReceiversWithStatus () {
        // given
         User sender1 = createUser("sender1");
         User sender2 = createUser("sender2");
         User sender3 = createUser("sender3");
         User sender4 = createUser("sender4");
         User receiver = createUser("receiver");

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

    private User createUser(String name) {
        User user = User.builder()
                .email(name + "@test.com")
                .name(name)
                .checkId(name + "id")
                .build();
        return userRepository.save(user);
    }

}