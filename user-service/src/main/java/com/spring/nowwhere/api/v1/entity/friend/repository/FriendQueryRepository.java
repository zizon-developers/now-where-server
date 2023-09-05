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

public interface FriendQueryRepository {

//    getSentFriendRequests(userId): 특정 사용자가 보낸 친구 요청을 조회하는 메서드
//    getFriends(userId): 특정 사용자의 친구 목록을 조회하는 메서드
}
