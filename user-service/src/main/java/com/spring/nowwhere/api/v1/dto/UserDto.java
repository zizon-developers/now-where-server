package com.spring.nowwhere.api.v1.dto;

import com.spring.nowwhere.api.v1.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private String email;
    private String name;
    private String userId;

    @Builder
    private UserDto(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static UserDto of(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getUserId())
                .build();
    }
}
