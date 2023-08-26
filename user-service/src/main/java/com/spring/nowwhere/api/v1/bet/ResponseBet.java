package com.spring.nowwhere.api.v1.bet;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "내기를 생성하고 응답하기 위한 DTO")
public class ResponseBet {

    @Schema(description = "내기를 요청한 사용자 ID")
    private Long bettorId;
    @Schema(description = "내기를 요청받을 사용자 ID")
    private Long receiverId;
    @Schema(description = "내기의 금액")
    private int amount;
    @Schema(description = "현재 내기의 진행상태")
    private BetStatus status;

    @Builder
    private ResponseBet(Long bettorId, Long receiverId, int amount, BetStatus status) {
        this.bettorId = bettorId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
    }

    public static ResponseBet of(Bet bet) {
        return ResponseBet.builder()
                .bettorId(bet.getBettor().getId())
                .receiverId(bet.getReceiver().getId())
                .amount(bet.getAmount())
                .status(bet.getStatus())
                .build();
    }
}
