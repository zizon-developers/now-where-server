package com.spring.nowwhere.api.v1.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.nowwhere.api.v1.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String email;
    private String name;
    private String userId;

    @Builder
    private UserResponse(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static UserResponse of(UserDto userDto){
        return UserResponse.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .build();
    }
}
