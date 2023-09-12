package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto {
    private String email;
    private String name;
    private String profileImg;

    @QueryProjection
    public UserInfoDto(String email, String name, String profileImg) {
        this.email = email;
        this.name = name;
        this.profileImg = profileImg;
    }
}
