package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "사용자는 내기 정보 변경 요청DTO")
@Getter
public class RejectBetRequest {

    @Schema(description = "내기를 요청한 사용자ID")
    private String bettorId;
    @Schema(description = "설정된 내기 시간")
    private BetDateTime betDateTime;

    @Builder
    private RejectBetRequest(String bettorId, BetDateTime betDateTime) {
        this.bettorId = bettorId;
        this.betDateTime = betDateTime;
    }
}
