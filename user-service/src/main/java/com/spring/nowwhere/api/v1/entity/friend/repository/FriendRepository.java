package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendID;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, FriendID> {
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
    @Query("select distinct f from Friend f left join fetch User u on u.id in (f.sender.id, f.receiver.id)"+
            "where (f.sender = :sender and f.receiver = :receiver) " +
            "or (f.sender = :receiver and f.receiver = :sender)")
    List<Friend> findByFriendWithReverse(@Param("sender") User sender,
                                         @Param("receiver") User receiver);
    Page<Friend> findByReceiverAndFriendStatus(User receiver, FriendStatus friendStatus, Pageable pageable);
    Page<Friend> findBySenderAndFriendStatus(User sender, FriendStatus friendStatus, Pageable pageable);

//    getSentFriendRequests(userId): 특정 사용자가 보낸 친구 요청을 조회하는 메서드
//    getFriends(userId): 특정 사용자의 친구 목록을 조회하는 메서드
}
