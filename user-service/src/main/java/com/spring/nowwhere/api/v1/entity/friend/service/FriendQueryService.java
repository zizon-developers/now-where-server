package com.spring.nowwhere.api.v1.entity.friend.service;

import com.spring.nowwhere.api.v1.entity.friend.dto.FriendDto;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public Page<UserDto> findFriendList(String userId, Pageable pageable){
        User user = validateUserOrThrowException(userId);
        List<UserDto> friends = user.getFriends().stream()
                                    .map(UserDto::of)
                                    .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), friends.size());
        return new PageImpl<>(friends.subList(start, end), pageable, friends.size());
    }

    private User validateUserOrThrowException(String receiverId) {
        return userRepository.findByCheckId(receiverId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
    }
}
