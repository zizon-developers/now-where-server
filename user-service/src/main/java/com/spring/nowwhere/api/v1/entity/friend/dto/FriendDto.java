package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendDto {
    private User user;
    private User friend;
    private FriendStatus friendStatus;

    @Builder
    private FriendDto(User user, User friend, FriendStatus friendStatus) {
        this.user = user;
        this.friend = friend;
        this.friendStatus = friendStatus;
    }

    public static FriendDto of(Friend friend) {
        return FriendDto.builder()
                        .user(friend.getSender())
                        .friend(friend.getReceiver())
                        .friendStatus(friend.getFriendStatus())
                        .build();
    }
}
