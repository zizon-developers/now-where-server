package com.spring.nowwhere.api.v1.entity.friend.service;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.exception.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;

    public void createFriendRequest (User sender, User receiver){ //친구 요청을 보내는 메서드

        Optional<Friend> findFriend = friendRepository.areFriends(sender, receiver);
        if (findFriend.isPresent()){
            FriendStatus friendStatus = findFriend.get().getFriendStatus();
            if (FriendStatus.COMPLETED.equals(friendStatus)){
                throw new AlreadyFriendsException("이미 친구상태 입니다.");
            } else if (FriendStatus.PENDING.equals(friendStatus)){
                throw new FriendRequestPendingException("요청 대기상태 입니다.");
            }
        }
        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();

        friendRepository.save(friend);
    }
//    rejectFriendRequest(requestId): 친구 요청을 거절하는 메서드
//    acceptFriendRequest(requestId): 친구 요청을 수락하는 메서드
}
