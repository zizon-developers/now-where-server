package com.spring.nowwhere.api.v1.entity.friend;

import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FriendTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("친구 요청에 대한 상태를 변경할 수 있다.")
    public void updateFriendStatus() {
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
        findFriend.updateFriendStatus(FriendStatus.DENIED_REQUEST);
        // then
        Friend updateFriend = friendRepository.areFriends(sender, receiver).get();
        Assertions.assertAll(
                ()->assertEquals(updateFriend.getFriendStatus(),FriendStatus.DENIED_REQUEST),
                ()->assertEquals(updateFriend.getSender(),findFriend.getSender()),
                ()->assertEquals(updateFriend.getReceiver(),findFriend.getReceiver())
        );
    }

    private User createAndSaveUser(String name) {
        User user = User.builder()
                .email(name+"@test.com")
                .checkId(name+"Id")
                .name(name)
                .build();
        return userRepository.save(user);
    }
}