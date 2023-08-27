package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import com.spring.nowwhere.api.v1.entity.user.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.user.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.user.entity.Friend;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public void sendFriendRequest(User sender, User receiver){ //친구 요청을 보내는 메서드

        Optional<Friend> friend = friendRepository.areFriends(sender, receiver);

        if (friend.isPresent()){
            if (friend.get().isFriend()){
                throw new AlreadyFriendsException("이미 친구상태 입니다.");
            }
            throw new FriendRequestPendingException("아직 요청 대기상태 입니다.");
        }

        Friend fromUserFriend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .isFriend(true)
                .build();

        Friend toUserFriend = Friend.builder()
                .sender(receiver)
                .receiver(sender)
                .isFriend(false)
                .build();

        friendRepository.saveAll(List.of(fromUserFriend, toUserFriend));
    }

//    rejectFriendRequest(requestId): 친구 요청을 거절하는 메서드
//    acceptFriendRequest(requestId): 친구 요청을 수락하는 메서드
}
