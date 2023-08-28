package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import com.spring.nowwhere.api.v1.entity.user.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.user.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.user.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.entity.Friend;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    @Transactional
    public void sendFriendRequest(User sender, User receiver){ //친구 요청을 보내는 메서드

        Optional<Friend> friend = friendRepository.areFriends(sender, receiver);
        if (friend.isPresent()){
            FriendStatus friendStatus = friend.get().getFriendStatus();
            if (FriendStatus.COMPLETED.equals(friendStatus)){
                throw new AlreadyFriendsException("이미 친구상태 입니다.");
            } else if (FriendStatus.PENDING.equals(friendStatus)){
                throw new FriendRequestPendingException("아직 요청 대기상태 입니다.");
            }
        }

        Friend fromUserFriend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.COMPLETED)
                .build();

        friendRepository.save(fromUserFriend);

        //역방향 레코드 insert 해주기 saveAll 하면 ManyToOne이라서 제약조건 걸림
        Friend toUserFriend = Friend.builder()
                .sender(receiver)
                .receiver(sender)
                .friendStatus(FriendStatus.PENDING)
                .build();

        friendRepository.save(toUserFriend);
    }

//    rejectFriendRequest(requestId): 친구 요청을 거절하는 메서드
//    acceptFriendRequest(requestId): 친구 요청을 수락하는 메서드
}
