package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetSummaryDto {

    private UserInfoDto userInfoDto;
    private Integer totalBetCount;
    private Integer totalBetAmount;

    @QueryProjection
    public BetSummaryDto(UserInfoDto userInfoDto, Integer totalBetCount, Integer totalBetAmount) {
        this.userInfoDto = userInfoDto;
        this.totalBetCount = totalBetCount;
        this.totalBetAmount = totalBetAmount;
    }
}
