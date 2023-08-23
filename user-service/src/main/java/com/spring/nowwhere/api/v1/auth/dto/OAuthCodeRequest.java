package com.spring.nowwhere.api.v1.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카카오 서버 요청을 위한 요청DTO")
public class OAuthCodeRequest {

    @Schema(description = "사용자 동의하기 이후 카카오 서버 요청을 위한 code값")
    String code;

    public String getCode() {
        return code;
    }
}
