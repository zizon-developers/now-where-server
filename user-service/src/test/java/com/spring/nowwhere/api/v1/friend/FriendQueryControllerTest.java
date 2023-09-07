package com.spring.nowwhere.api.v1.friend;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FriendQueryControllerTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown(){
        friendRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

//    @Test
//    @DisplayName("친구 요청 목록을 조회할 수 있다.")
//    public void findFriendRequests() {
//        // given
//        User sender = createAndSaveUser("sender");
//        User receiver1 = createAndSaveUser("receiver1");
//        User receiver2 = createAndSaveUser("receiver2");
//        User receiver3 = createAndSaveUser("receiver3");
//        createAndSaveFriend(sender, receiver1, FriendStatus.PENDING);
//        createAndSaveFriend(sender, receiver2, FriendStatus.PENDING);
//        createAndSaveFriend(sender, receiver3, FriendStatus.PENDING);
//        int size = 2;
//        // when
//        int page = 0;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        System.out.println("=============");
//        Page<FriendDto> friendRequests = friendQueryService.findFriendRequests(sender.getCheckId(), pageRequest);
//        System.out.println("=============");
//        // then
//        List<FriendDto> findFriendDto = friendRequests.getContent();
//        Assertions.assertThat(friendRequests.getTotalElements()).isEqualTo(3L);
//        Assertions.assertThat(friendRequests.hasNext()).isTrue();
//
//        Assertions.assertThat(findFriendDto).hasSize(size)
//                .extracting("user", "friend", "friendStatus")
//                .containsExactlyInAnyOrder(
//                        tuple(sender, receiver1, FriendStatus.PENDING),
//                        tuple(sender, receiver2, FriendStatus.PENDING)
//                );
//    }
//
//    @Test
//    @DisplayName("친구 목록을 조회할 수 있다.")
//    public void findFriendList() {
//        // given
//        User sender = createAndSaveUser("sender");
//        User receiver1 = createAndSaveUser("receiver1");
//        User receiver2 = createAndSaveUser("receiver2");
//        User receiver3 = createAndSaveUser("receiver3");
//
//        createAndSaveFriend(sender, receiver1, FriendStatus.COMPLETED);
//        createAndSaveFriend(sender, receiver2, FriendStatus.COMPLETED);
//        createAndSaveFriend(sender, receiver3, FriendStatus.COMPLETED);
//        int size = 2;
//        // when
//        int page = 0;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        Page<FriendDto> friendRequests = friendQueryService.findFriendList(sender.getCheckId(), pageRequest);
//        // then
//        List<FriendDto> findFriendDto = friendRequests.getContent();
//        Assertions.assertThat(friendRequests.getTotalElements()).isEqualTo(3L);
//        Assertions.assertThat(friendRequests.hasNext()).isTrue();
//
//        Assertions.assertThat(findFriendDto).hasSize(size)
//                .extracting("user", "friend", "friendStatus")
//                .containsExactlyInAnyOrder(
//                        tuple(sender, receiver1, FriendStatus.COMPLETED),
//                        tuple(sender, receiver2, FriendStatus.COMPLETED)
//                );
//    }

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