package com.spring.nowwhere.api.v1.bet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "내기를 생성하기 위한 요청DTO")
public class RequestBetDto {

    @Schema(description = "내기를 요청받을 사용자ID")
    private String receiverId;
    @Schema(description = "내기 금액을 위한 필드", minimum = "1")
    private int amount;
}
