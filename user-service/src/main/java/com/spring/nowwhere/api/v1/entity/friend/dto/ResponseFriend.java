package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "친구 목록 조회 응답 DTO")
public class ResponseFriend {
    @Schema(description = "친구 이메일")
    private String email;
    @Schema(description = "친구 이름")
    private String name;
    @Schema(description = "친구 프로필 이미지")
    private String profileImg;
    @Builder
    private ResponseFriend(String email, String name, String profileImg) {
        this.email = email;
        this.name = name;
        this.profileImg = profileImg;
    }
    public static ResponseFriend of(UserDto userDto) {
        return ResponseFriend.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .profileImg(userDto.getProfileImg())
                .build();
    }
}
