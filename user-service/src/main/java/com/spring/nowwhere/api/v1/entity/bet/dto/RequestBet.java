package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.spring.nowwhere.api.v1.entity.bet.BetInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "내기를 생성하기 위한 요청DTO")
public class RequestBet {

    @Schema(description = "내기를 요청받을 사용자ID")
    private String receiverId;

    @Schema(description = "내기 정보들")
    private BetInfo betInfo;

    @Builder
    private RequestBet(String receiverId, BetInfo betInfo) {
        this.receiverId = receiverId;
        this.betInfo = betInfo;
    }
}
