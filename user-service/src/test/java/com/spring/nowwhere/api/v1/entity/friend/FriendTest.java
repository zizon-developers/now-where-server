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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FriendTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("친구 요청에 대한 상태를 변경할 수 있다.")
    public void updateFriendStatus() {
        // given
        User sender = createUser("sender");
        User receiver = createUser("receiver");
        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);
        // when
        Friend findFriend = friendRepository.areFriends(sender, receiver).get();
        findFriend.updateFriendStatus(FriendStatus.DENIED_REQUEST);
        Friend updateFriend = friendRepository.areFriends(sender, receiver).get();
        // then
        Assertions.assertAll(
                ()->assertEquals(updateFriend.getFriendStatus(),FriendStatus.DENIED_REQUEST),
                ()->assertEquals(updateFriend.getSender(),findFriend.getSender()),
                ()->assertEquals(updateFriend.getReceiver(),findFriend.getReceiver())
        );

    }

    private User createUser(String name) {
        User user = User.builder()
                .email(name+"@test.com")
                .checkId(name+"Id")
                .name(name)
                .build();
        userRepository.save(user);
        return user;

    }
}