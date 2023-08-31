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
@Transactional
public class FriendService {
    private final int SENDER_INDEX = 0;
    private final int RECEIVER_INDEX = 1;

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public void createFriendRequest (String senderId, String receiverId){ //친구 요청을 보내는 메서드

        List<User> senderAndReceiver = checkSenderAndReceiver(senderId, receiverId);
        User sender = senderAndReceiver.get(SENDER_INDEX);
        User receiver = senderAndReceiver.get(RECEIVER_INDEX);

        Optional<Friend> findFriend = friendRepository.areFriends(sender, receiver);
        if (findFriend.isPresent()){
            FriendStatus friendStatus = findFriend.get().getFriendStatus();
            if (FriendStatus.COMPLETED.equals(friendStatus)){
                throw new AlreadyFriendsException("이미 친구상태 입니다.");
            } else if (FriendStatus.PENDING.equals(friendStatus)){
                throw new FriendRequestPendingException("요청 대기상태 입니다.");
            }
        }

        //다음에 DTO 변환해주기
        Friend friend = createAndSaveFriend(sender, receiver);
    }
    private List<User> checkSenderAndReceiver(String senderId, String receiverId) {
        List<User> senderAndReceiver = userRepository.findSenderAndReceiver(senderId, receiverId);
        if (senderAndReceiver.size() != 2)
            throw new UsernameNotFoundException("sender and receiver is not found");

        return senderAndReceiver;
    }

    private Friend createAndSaveFriend(User sender, User receiver) {
        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();

        friendRepository.save(friend);
        return friend;
    }

    //친구 요청을 수락하는 메서드
    public void acceptFriendRequest (String receiverId){
        User receiver = checkUser(receiverId);

        Friend findFriend = checkReceiverPendingStatus(receiver);
        findFriend.updateFriendStatus(FriendStatus.COMPLETED);

        insertReceiverInverseRecord(findFriend.getSender(), receiver);
    }
    //친구 요청을 거절하는 메서드
    public void rejectFriendRequest (String receiverId){
        User receiver = checkUser(receiverId);

        Friend findFriend = checkReceiverPendingStatus(receiver);
        findFriend.updateFriendStatus(FriendStatus.DENIED_REQUEST);
        //나중에 추천친구 로직에 확률 계산하기 위한 로직 추가하기
    }

    private User checkUser(String receiverId) {
        return userRepository.findByCheckId(receiverId)
                .orElseThrow(() -> new UsernameNotFoundException("가입하지 않은 사용자 입니다."));
    }
    private Friend checkReceiverPendingStatus(User receiver) {
        return friendRepository.getReceiversWithStatus(receiver, FriendStatus.PENDING)
                .orElseThrow(() -> new FriendRequestPendingException("친구 요청을 응답할 수 있는 상태가 아닙니다."));
    }

    private Friend insertReceiverInverseRecord(User sender, User receiver) {
        Friend receiverFriend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .friendStatus(FriendStatus.PENDING)
                .build();

        return friendRepository.save(receiverFriend);
    }
    public void cancelFriendRequest (String senderId){
        User sender = checkUser(senderId);

        //동적 sql로 처리해도 될듯..?
        Friend findFriend = checkSenderPendingStatus(sender);
        findFriend.updateFriendStatus(FriendStatus.CANCELED_REQUEST);
        //나중에 추천친구 로직에 확률 계산하기 위한 로직 추가하기
    }
    private Friend checkSenderPendingStatus(User sender) {
        return friendRepository.getSendersWithStatus(sender, FriendStatus.PENDING)
                .orElseThrow(() -> new FriendRequestPendingException("친구 요청을 응답할 수 있는 상태가 아닙니다."));
    }
}
