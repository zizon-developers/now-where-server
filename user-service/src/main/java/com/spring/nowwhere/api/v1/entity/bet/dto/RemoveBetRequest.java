package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import com.spring.nowwhere.api.v1.entity.bet.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Embedded;
import java.util.Optional;

@Schema(description = "사용자는 내기 정보 변경 요청DTO")
@Getter
public class RemoveBetRequest {

    @Schema(description = "내기를 요청받을 사용자ID")
    String receiverId;
    @Schema(description = "설정된 내기 시간")
    BetDateTime betDateTime;

    @Builder
    private RemoveBetRequest(String receiverId, BetDateTime betDateTime) {
        this.receiverId = receiverId;
        this.betDateTime = betDateTime;
    }
}
