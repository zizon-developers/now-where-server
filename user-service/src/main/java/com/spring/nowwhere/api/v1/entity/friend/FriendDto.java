package com.spring.nowwhere.api.v1.entity.friend;

import com.spring.nowwhere.api.v1.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.persistence.*;
@Schema(description = "친구 요청 목록 조회를 위한 요청DTO")
public class FriendDto {
    private User sender;
    private User receiver;
//    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus;

    @Builder
    private FriendDto(User sender, User receiver, FriendStatus friendStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendStatus = friendStatus;
    }

    public static FriendDto of(Friend friend) {
        return FriendDto.builder()
                        .sender(friend.getSender())
                        .receiver(friend.getReceiver())
                        .friendStatus(friend.getFriendStatus())
                        .build();
    }
}
