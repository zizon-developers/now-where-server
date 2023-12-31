package com.spring.nowwhere.api.v1.entity.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.nowwhere.api.v1.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String email;
    private String name;
    private String checkId;

    @Builder
    private UserResponse(String email, String name, String checkId) {
        this.email = email;
        this.name = name;
        this.checkId = checkId;
    }

    public static UserResponse of(UserDto userDto){
        return UserResponse.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .checkId(userDto.getCheckId())
                .build();
    }
    public static UserResponse of(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .checkId(user.getCheckId())
                .build();
    }
}
