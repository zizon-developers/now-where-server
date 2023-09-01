package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.spring.nowwhere.api.v1.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "친구 요청 목록 조회 응답 DTO")
public class ResponseFriendRequest {
    @Schema(description = "친구")
    private User friend;

    public ResponseFriendRequest(FriendDto friend) {
        this.friend = friend.getFriend();
    }
}
