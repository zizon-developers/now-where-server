package com.spring.nowwhere.api.v1.entity.friend;

import com.spring.nowwhere.api.v1.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Objects;

@Schema(description = "친구 요청 목록 조회를 위한 요청DTO")
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
