package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.QFriend;
import com.spring.nowwhere.api.v1.entity.user.QUser;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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
    public void findBySenderAndReceiver() {
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
        Friend findFriend = friendRepository.findBySenderAndReceiver(sender, receiver).get();
        // then
        assertAll(
                () -> assertEquals(findFriend.getFriendStatus(), FriendStatus.PENDING),
                () -> assertEquals(findFriend.getReceiver(), receiver),
                () -> assertEquals(findFriend.getSender(), sender)
        );
    }
    @DisplayName("친구 요청을 보낸 사용자가 특정 상태에서 친구 요청 받은 정보를 조회할 수 있다.")
//    @TestFactory
    Collection<DynamicTest> findBySenderAndFriendStatus () {
        // given
        User sender = createAndSaveUser("sender");
        User receiver1 = createAndSaveUser("receiver1");
        User receiver2 = createAndSaveUser("receiver2");
        User receiver3 = createAndSaveUser("receiver3");
        User receiver4 = createAndSaveUser("receiver4");

        createAndSaveFriend(sender, receiver1, FriendStatus.PENDING);
        createAndSaveFriend(sender, receiver2, FriendStatus.DENIED_REQUEST);
        createAndSaveFriend(sender, receiver3, FriendStatus.CANCELED_REQUEST);
        createAndSaveFriend(sender, receiver4, FriendStatus.COMPLETED);
        PageRequest pageRequest = PageRequest.of(0, 1);
        return List.of(
                DynamicTest.dynamicTest("친구 요청이 PENDING 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Page<Friend> friends = friendRepository.findBySenderAndFriendStatus(sender, FriendStatus.PENDING, pageRequest);
                    //then
                    assertThat(friends).hasSize(1)
                            .extracting("receiver","sender","friendStatus")
                            .containsExactly(
                                    tuple(receiver1,sender,FriendStatus.PENDING)
                            );
                }),
                DynamicTest.dynamicTest("친구 요청이 DENIED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Page<Friend> friends = friendRepository.findBySenderAndFriendStatus(sender, FriendStatus.DENIED_REQUEST,pageRequest);
                    //then
                    assertThat(friends).hasSize(1)
                            .extracting("receiver","sender","friendStatus")
                            .containsExactly(
                                    tuple(receiver2,sender,FriendStatus.DENIED_REQUEST)
                            );
                }),
                DynamicTest.dynamicTest("친구 요청이 CANCELED_REQUEST 상태인 경우를 조회할 수 있다.", () -> {
                    //when
                    Page<Friend> friends = friendRepository.findBySenderAndFriendStatus(sender, FriendStatus.CANCELED_REQUEST,pageRequest);
                    //then
                    assertThat(friends).hasSize(1)
                            .extracting("receiver","sender","friendStatus")
                            .containsExactly(
                                    tuple(receiver3,sender,FriendStatus.CANCELED_REQUEST)
                            );
                }),
                DynamicTest.dynamicTest("친구 요청이 COMPLETED 상태인 경우를 조회할 수 있다.", () -> {
                    Page<Friend> friends = friendRepository.findBySenderAndFriendStatus(sender, FriendStatus.COMPLETED,pageRequest);
                    //then
                    assertThat(friends).hasSize(1)
                            .extracting("receiver","sender","friendStatus")
                            .containsExactly(
                                    tuple(receiver4,sender,FriendStatus.COMPLETED)
                            );
                })
        );
    }

//    @Test
    @DisplayName("친구 조회시 역방향 레코드도 같이 조회한다.")
    public void findByFriendWithReverse() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        User test = createAndSaveUser("test");
        createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);
        createAndSaveFriend(receiver, sender, FriendStatus.COMPLETED);
        createAndSaveFriend(sender, test, FriendStatus.COMPLETED);
        createAndSaveFriend(test, sender, FriendStatus.COMPLETED);
        // when
        System.out.println("=========");
        List<Friend> friendWithReverse = friendRepository.findByFriendWithReverse(sender, receiver);
        System.out.println("=========");
        // then
        assertThat(friendWithReverse).hasSize(2)
                .extracting("sender", "receiver", "friendStatus")
                .containsExactlyInAnyOrder(
                        tuple(sender, receiver, FriendStatus.COMPLETED),
                        tuple(receiver, sender, FriendStatus.COMPLETED)
                );
    }
    @Autowired
    EntityManager em;
    @PersistenceUnit
    EntityManagerFactory emf;
//    @Test
    @DisplayName("test")
    public void test() {
        // given
        User sender = createAndSaveUser("sender");
        User receiver = createAndSaveUser("receiver");
        User test = createAndSaveUser("test");
        createAndSaveFriend(sender, receiver, FriendStatus.COMPLETED);
        createAndSaveFriend(receiver, sender, FriendStatus.COMPLETED);
        createAndSaveFriend(sender, test, FriendStatus.COMPLETED);
        createAndSaveFriend(test, sender, FriendStatus.COMPLETED);
        em.flush();
        em.clear();
        // when
        System.out.println("========="); //exist 사용해도될듯 DTO로 따로 뺴서 조회하기
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> fetch = queryFactory.select(QFriend.friend.sender, QFriend.friend.receiver).distinct()
                .from(QFriend.friend)
                .join(QUser.user).fetchJoin()
                .on(QUser.user.in(sender, receiver))
                .where((QFriend.friend.sender.eq(sender).and(QFriend.friend.receiver.eq(receiver)))
                        .or(QFriend.friend.sender.eq(receiver).and(QFriend.friend.receiver.eq(sender))))
                .fetch();

        System.out.println("=========");
        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
//        boolean loaded1 = emf.getPersistenceUnitUtil().isLoaded(fetch.get(0).getSender());
//        boolean loaded2 = emf.getPersistenceUnitUtil().isLoaded(fetch.get(0).getReceiver());
//        System.out.println("loaded1 = " + loaded1);
//        System.out.println("loaded2 = " + loaded2);

    }

    private User createAndSaveUser(String name) {
        User user = User.builder()
                .email(name + "@test.com")
                .name(name)
                .checkId(name + "id")
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