package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.entity.friend.*;
import com.spring.nowwhere.api.v1.entity.friend.exception.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
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
        Collection<DynamicTest> createFriendRequest() {
            // given
            return List.of(
                    DynamicTest.dynamicTest("사용자는 다른 사용자들에게 친구추가를 요청할 수 있다.", () -> {
                        //given
                        List<User> senderAndReceiver = createUser("sender1", "receiver1");
                        List<User> receivers = createUser("receiver2", "receiver3");
                        User sender = senderAndReceiver.get(0);
                        User receiver1 = senderAndReceiver.get(1);
                        User receiver2 = receivers.get(0);
                        User receiver3 = receivers.get(1);

                        //when
                        friendService.createFriendRequest(sender.getCheckId(),receiver1.getCheckId());
                        friendService.createFriendRequest(sender.getCheckId(),receiver2.getCheckId());
                        friendService.createFriendRequest(receiver2.getCheckId(),receiver3.getCheckId());
                        //then
                        Friend friend1 = friendRepository.areFriends(sender, receiver1).get();
                        Friend friend2 = friendRepository.areFriends(sender, receiver2).get();
                        Friend friend3 = friendRepository.areFriends(receiver2, receiver3).get();
                        assertAll(
                                //역방향 레코드 조회 주의하기
                                () -> assertEquals(friend1.getSender(),sender),
                                () -> assertEquals(friend1.getReceiver(),receiver1),
                                () -> assertEquals(friend1.getFriendStatus(), FriendStatus.PENDING),

                                () -> assertEquals(friend2.getSender(),sender),
                                () -> assertEquals(friend2.getReceiver(),receiver2),
                                () -> assertEquals(friend2.getFriendStatus(), FriendStatus.PENDING),

                                () -> assertEquals(friend3.getSender(),receiver2),
                                () -> assertEquals(friend3.getReceiver(),receiver3),
                                () -> assertEquals(friend3.getFriendStatus(), FriendStatus.PENDING)
                        );
                    }),
                    DynamicTest.dynamicTest("요청 대기상태에서 친구 요청시 예외가 발생한다.", () -> {
                        //given
                        List<User> users = createUser("sender2", "receiver4");
                        User sender = users.get(0);
                        User receiver = users.get(1);

                        Friend friend = Friend.builder()
                                .sender(sender)
                                .receiver(receiver)
                                .friendStatus(FriendStatus.PENDING)
                                .build();

                        friendRepository.save(friend);
                        //when //then
                        Assertions.assertThatThrownBy(() -> friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId()))
                                .isInstanceOf(FriendRequestPendingException.class)
                                .hasMessage("요청 대기상태 입니다.");
                    }),
                    DynamicTest.dynamicTest("이미 친구인 상태에서 친구 요청시 예외가 발생한다.", () -> {
                        //given
                        List<User> users = createUser("sender3", "receiver5");
                        User sender = users.get(0);
                        User receiver = users.get(1);

                        Friend friend = Friend.builder()
                                .sender(sender)
                                .receiver(receiver)
                                .friendStatus(FriendStatus.COMPLETED)
                                .build();

                        friendRepository.save(friend);
                        //when //then
                        Assertions.assertThatThrownBy(() -> friendService.createFriendRequest(sender.getCheckId(), receiver.getCheckId()))
                                .isInstanceOf(AlreadyFriendsException.class)
                                .hasMessage("이미 친구상태 입니다.");
                    })
            );
        }
    private List<User> createUser(String name1, String name2){
        User user1 = User.builder()
                .email(name1+"@test.com")
                .name(name1)
                .checkId(name1+"Id")
                .build();

        User user2 = User.builder()
                .email(name2+"@test.com")
                .name(name2)
                .checkId(name2+"id")
                .build();
        userRepository.saveAll(List.of(user1, user2));
        return List.of(user1, user2);
    }
}