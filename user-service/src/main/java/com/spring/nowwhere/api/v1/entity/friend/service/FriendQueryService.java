package com.spring.nowwhere.api.v1.entity.friend.service;

import com.spring.nowwhere.api.v1.entity.friend.FriendDto;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendQueryService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public Page<FriendDto> findFriendRequests(String userId, Pageable pageable){
        User user = validateUserOrThrowException(userId);

        return  friendRepository.findBySenderAndFriendStatus(user, FriendStatus.PENDING, pageable)
                                .map(FriendDto::of);
    }

    public Page<FriendDto> findFriendList(String userId, Pageable pageable){
        User user = validateUserOrThrowException(userId);

        return  friendRepository.findBySenderAndFriendStatus(user, FriendStatus.COMPLETED, pageable)
                                .map(FriendDto::of);
    }

    private User validateUserOrThrowException(String receiverId) {
        return userRepository.findByCheckId(receiverId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
    }
}
