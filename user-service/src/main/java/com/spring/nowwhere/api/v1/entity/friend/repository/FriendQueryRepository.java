package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.dto.FriendQueryDto;
import com.spring.nowwhere.api.v1.entity.friend.dto.ResponseFriendDto;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FriendQueryRepository {
    Page<FriendQueryDto> findBySenderAndFriendStatus(User sender, FriendStatus friendStatus, Pageable pageable);
    List<Friend> findByFriendWithReverse(User sender, User receiver);
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
}
