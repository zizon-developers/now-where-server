package com.spring.nowwhere.api.v1.entity.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "친구 요청을 위한 요청DTO")
public class RequestFriend {

    @Schema(description = "친구 요청받을 사용자ID")
    private String receiverId;

    public RequestFriend(String receiverId) {
        this.receiverId = receiverId;
    }
    public String getReceiverId() {
        return receiverId;
    }
}
