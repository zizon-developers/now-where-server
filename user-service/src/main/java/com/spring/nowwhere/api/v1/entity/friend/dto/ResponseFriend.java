package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "친구 목록 조회 응답 DTO")
public class ResponseFriend {
    @Schema(description = "친구")
    private User friend;
    public ResponseFriend(FriendDto friend) {
        this.friend = friend.getFriend();
    }
}
