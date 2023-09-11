package com.spring.nowwhere.api.v1.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Schema(description = "카카오 링크 가입을 위한 요청DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteFriendRequest {
    @Schema(description = "회원가입하는 사용자 카카오 code")
    String code;

    @Schema(description = "친구 링크를 보낸 사용자 id")
    String checkId;

    public String getCode() {
        return code;
    }

    public String getCheckId() {
        return checkId;
    }
}
