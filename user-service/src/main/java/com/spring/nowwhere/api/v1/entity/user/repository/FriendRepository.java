package com.spring.nowwhere.api.v1.entity.user.repository;

import com.spring.nowwhere.api.v1.entity.user.entity.Friend;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long> {
    @Query("select f from Friend f where f.receiver = :sender " +
            "and f.sender = :receiver")
    Optional<Friend> areFriends(@Param("sender") User sender,
                                @Param("receiver") User receiver);
//    getSentFriendRequests(userId): 특정 사용자가 보낸 친구 요청을 조회하는 메서드
//    getReceivedFriendRequests(userId): 특정 사용자가 받은 친구 요청을 조회하는 메서드
//    getFriends(userId): 특정 사용자의 친구 목록을 조회하는 메서드
}
