package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "사용자 정보를 가지고있는 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto {
    @Schema(description = "사용자 이메일")
    private String email;
    @Schema(description = "사용자 이름")
    private String name;
    @Schema(description = "사용자 프로필 이미지 주소")
    private String profileImg;

    @QueryProjection
    public UserInfoDto(String email, String name, String profileImg) {
        this.email = email;
        this.name = name;
        this.profileImg = profileImg;
    }
}
