package com.spring.nowwhere.api.v1.entity.bet.geo.dto;

import com.spring.nowwhere.api.v1.entity.bet.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "내기 위치 정보를 위한 응답 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetRefreshDto {
    private Location userInfo;
    private Location destinationInfo;

    public BetRefreshDto(Location userInfo, Location destinationInfo) {
        this.userInfo = userInfo;
        this.destinationInfo = destinationInfo;
    }
}
