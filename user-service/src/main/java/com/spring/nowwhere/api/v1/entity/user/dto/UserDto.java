package com.spring.nowwhere.api.v1.entity.user.dto;

import com.spring.nowwhere.api.v1.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private String email;
    private String name;
    private String checkId;
    private String profileImg;

    @Builder
    private UserDto(String email, String name, String checkId, String profileImg) {
        this.email = email;
        this.name = name;
        this.checkId = checkId;
        this.profileImg = profileImg;
    }

    public static UserDto of(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .name(user.getName())
                .checkId(user.getCheckId())
                .build();
    }
}
