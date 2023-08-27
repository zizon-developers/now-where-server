package com.spring.nowwhere.api.v1.entity.bet;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "내기를 생성하고 응답하기 위한 DTO")
public class ResponseBet {

    @Schema(description = "내기를 요청한 사용자 ID")
    private String bettorId;
    @Schema(description = "내기를 요청받을 사용자 ID")
    private String receiverId;
    @Schema(description = "내기 정보")
    private BetInfo betInfo;
    @Schema(description = "현재 내기의 진행상태")
    private BetStatus betStatus;

    @Builder
    private ResponseBet(String bettorId, String receiverId, BetInfo betInfo, BetStatus betStatus) {
        this.bettorId = bettorId;
        this.receiverId = receiverId;
        this.betInfo = betInfo;
        this.betStatus = betStatus;
    }

    public static ResponseBet of(Bet bet) {
        return ResponseBet.builder()
                .bettorId(bet.getBettor().getCheckId())
                .receiverId(bet.getReceiver().getCheckId())
                .betInfo(bet.getBetInfo())
                .betStatus(bet.getBetStatus())
                .build();
    }
}
