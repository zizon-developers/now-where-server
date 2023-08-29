package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                () -> assertEquals(findFriend.getFriendStatus(),FriendStatus.PENDING),
                () -> assertEquals(findFriend.getReceiver(),receiver),
                () -> assertEquals(findFriend.getSender(),sender)
        );
    }
    private User createUser(String name){
        User user = User.builder()
                .email(name+"@test.com")
                .name(name)
                .checkId(name+"id")
                .build();
        return userRepository.save(user);
    }

}