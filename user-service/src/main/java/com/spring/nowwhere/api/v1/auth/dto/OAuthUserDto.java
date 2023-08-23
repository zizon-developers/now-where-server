package com.spring.nowwhere.api.v1.auth.dto;

import com.spring.nowwhere.api.v1.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "카카오 계정 응답DTO")
public class OAuthUserDto{

    @Schema(description = "카카오 계정 이메일(이메일 동의X -> 고유ID 값으로 대체됩니다. 추가 동의시 변경됩니다.)")
    private String email;
    @Schema(description = "카카오 계정 닉네임")
    private String name;
    @Schema(description = "카카오 계정 고유 ID값")
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
