package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long> {
    @Query("select f from Friend f where f.receiver = :receiver " +
            "and f.sender = :sender")
    Optional<Friend> areFriends(@Param("sender") User sender,
                                @Param("receiver") User receiver);

    //특정 사용자가 받은 친구 요청을 조회하는 메서드 반환값 List임
    @Query("select f from Friend f where f.receiver = :receiver " +
            "and f.friendStatus = :status")
    Optional<Friend> getReceiversWithStatus(@Param("receiver") User receiver,
                                               @Param("status") FriendStatus friendStatus);

    @Query("select f from Friend f where f.sender = :sender " +
            "and f.friendStatus = :status")
    Optional<Friend> getSendersWithStatus(@Param("sender") User sender,
                                            @Param("status") FriendStatus friendStatus);

//    getSentFriendRequests(userId): 특정 사용자가 보낸 친구 요청을 조회하는 메서드
//    getFriends(userId): 특정 사용자의 친구 목록을 조회하는 메서드
}
