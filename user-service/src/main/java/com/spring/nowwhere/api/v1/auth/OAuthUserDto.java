package com.spring.nowwhere.api.v1.auth;

import com.spring.nowwhere.api.v1.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthUserDto {
    private String email;
    private String name;
    private String userId;

    @Builder
    private OAuthUserDto(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static OAuthUserDto of(User user) {
        return OAuthUserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getUserId())
                .build();
    }
}