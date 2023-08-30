package com.spring.nowwhere.api.v1.entity.friend.service;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.exception.AlreadyFriendsException;
import com.spring.nowwhere.api.v1.entity.friend.exception.FriendRequestPendingException;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createFriendRequest (String senderId, String receiverId){ //친구 요청을 보내는 메서드

        List<User> senderAndReceiver = checkSenderAndReceiver(senderId, receiverId);
        User sender = senderAndReceiver.get(0);
        User receiver = senderAndReceiver.get(1);

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

    //친구 요청을 수락하는 메서드
    @Transactional
    public void acceptFriendRequest (String senderId, String receiverId){
        List<User> senderAndReceiver = checkSenderAndReceiver(senderId, receiverId);
        User sender = senderAndReceiver.get(0);
        User receiver = senderAndReceiver.get(1);

        Friend findFriend = checkPendingStatus(sender, receiver);

        findFriend.updateFriendStatus(FriendStatus.COMPLETED);
        insertReceiverInverseRecord(sender, receiver);
    }

    private void insertReceiverInverseRecord(User sender, User receiver) {
        Friend receiverFriend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();

        friendRepository.save(receiverFriend);
    }

    //친구 요청을 거절하는 메서드
    @Transactional
    public void rejectFriendRequest (String senderId, String receiverId){
        List<User> senderAndReceiver = checkSenderAndReceiver(senderId, receiverId);
        User sender = senderAndReceiver.get(0);
        User receiver = senderAndReceiver.get(1);

        Friend findFriend = checkPendingStatus(sender, receiver);
        findFriend.updateFriendStatus(FriendStatus.DENIED_REQUEST);
        //나중에 추천친구 로직에 확률 계산하기 위한 로직 추가하기
    }
    @Transactional
    public void cancelFriendRequest (String senderId, String receiverId){
        List<User> senderAndReceiver = checkSenderAndReceiver(senderId, receiverId);
        User sender = senderAndReceiver.get(0);
        User receiver = senderAndReceiver.get(1);

        Friend findFriend = checkPendingStatus(sender, receiver);
        findFriend.updateFriendStatus(FriendStatus.CANCELED_REQUEST);
        //나중에 추천친구 로직에 확률 계산하기 위한 로직 추가하기
    }

    private Friend checkPendingStatus(User sender, User receiver) {
        return friendRepository.areFriends(sender, receiver)
                .filter(f -> FriendStatus.PENDING.equals(f.getFriendStatus()))
                .orElseThrow(() -> new FriendRequestPendingException("친구 요청을 응답할 수 있는 상태가 아닙니다."));
    }

    private List<User> checkSenderAndReceiver(String senderId, String receiverId) {
        List<User> senderAndReceiver = userRepository.findSenderAndReceiver(senderId, receiverId);
        if (senderAndReceiver.size() != 2)
            throw new UsernameNotFoundException("sender and receiver is not found");

        return senderAndReceiver;
    }
}
