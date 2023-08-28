package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.user.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.entity.Friend;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FriendRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;

    @AfterEach
    void tearDown(){
        friendRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("두 사용자의 친구 정보를 조회할 수 있다.")
    public void areFriends() {
        // given
        List<User> users = createUser("sender", "receiver");
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
        // when
        Friend friend = friendRepository.areFriends(sender, receiver).get();
        // then
        assertAll(
                () -> assertEquals(friend.getFriendStatus(),FriendStatus.PENDING),
                () -> assertEquals(friend.getReceiver(),toUserFriend.getReceiver()),
                () -> assertEquals(friend.getSender(),toUserFriend.getSender())
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