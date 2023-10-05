package com.spring.nowwhere.api.v1.redis.geo.dto;

import com.spring.nowwhere.api.v1.entity.bet.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.geo.Metrics;

@Getter
@Schema(description = "내기 위치 정보를 위한 응답 DTO")
public class ResponseBetGeo {

    @Schema(description = "도착지와 거리 차이 단위는 km")
    private double distance;
    @Schema(description = "사용자 id")
    private String userId;

    public ResponseBetGeo(double distance, String userId) {
        this.distance = distance;
        this.userId = userId;
    }
}
