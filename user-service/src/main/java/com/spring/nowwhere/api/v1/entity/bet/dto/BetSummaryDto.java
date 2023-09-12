package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "사용자 정보와 참여한 내기를 조회한 응답 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetSummaryDto {

    @Schema(description = "사용자 정보")
    private UserInfoDto userInfoDto;
    @Schema(description = "사용자가 참여한 내기 횟수")
    private Integer totalBetCount;
    @Schema(description = "사용자가 내기를 통해 얻은 총 금액")
    private Integer totalBetAmount;

    @QueryProjection
    public BetSummaryDto(UserInfoDto userInfoDto, Integer totalBetCount, Integer totalBetAmount) {
        this.userInfoDto = userInfoDto;
        this.totalBetCount = totalBetCount;
        this.totalBetAmount = totalBetAmount;
    }
}
