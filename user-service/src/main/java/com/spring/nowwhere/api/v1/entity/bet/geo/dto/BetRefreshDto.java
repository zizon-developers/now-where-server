package com.spring.nowwhere.api.v1.entity.bet.geo.dto;

import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import com.spring.nowwhere.api.v1.entity.bet.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "내기 위치 정보를 위한 응답 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetRefreshDto {

    @Schema(description = "도착지 정보")
    private Location destinationInfo;
    @Schema(description = "내기 시간 정보")
    private BetDateTime betDateTime;

    public BetRefreshDto(Location destinationInfo, BetDateTime betDateTime) {
        this.destinationInfo = destinationInfo;
        this.betDateTime = betDateTime;
    }
}
