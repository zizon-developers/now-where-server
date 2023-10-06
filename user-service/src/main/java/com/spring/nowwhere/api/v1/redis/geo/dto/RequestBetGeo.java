package com.spring.nowwhere.api.v1.redis.geo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "내기 위치 정보를 위한 요청 DTO")
public class RequestBetGeo {

    @Schema(description = "사용자 위도")
    private double latitude;
    @Schema(description = "사용자 경도")
    private double longitude; //경도
    @Schema(description = "도착지 이름")
    private String destination;
}
