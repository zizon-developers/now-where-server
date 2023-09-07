package com.spring.nowwhere.api.v1.entity.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "친구 목록 조회 응답 DTO")
public class ResponseFriendDto {
    @Schema(description = "친구 이름")
    String friendName;
    @Schema(description = "친구 프로필 이미지")
    String friendProfileImg;

    @Builder
    private ResponseFriendDto(String friendName, String friendProfileImg) {
        this.friendName = friendName;
        this.friendProfileImg = friendProfileImg;
    }

    public static ResponseFriendDto of(FriendQueryDto friendQueryDto) {
        return ResponseFriendDto.builder()
                .friendName(friendQueryDto.getFriendName())
                .friendProfileImg(friendQueryDto.getFriendProfileImg())
                .build();
    }
}
