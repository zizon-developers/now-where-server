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
public class UpdateBetRequest {

    @Schema(description = "내기를 요청받을 사용자ID")
    String receiverId;
    @Schema(description = "기존 내기 시간")
    BetDateTime betDateTime;
    @Schema(description = "변경한 내기 정보들")
    UpdateInfoRequest updateBetInfoRequest;

    @Builder
    private UpdateBetRequest(String receiverId, BetDateTime betDateTime, UpdateInfoRequest updateBetInfoRequest) {
        this.receiverId = receiverId;
        this.betDateTime = betDateTime;
        this.updateBetInfoRequest = updateBetInfoRequest;
    }
    @Getter
    public static class UpdateInfoRequest {
        @Schema(description = "내기 시간")
        private Optional<BetDateTime> betDateTime;

        @Schema(description = "내기 금액", minimum = "0")
        private Optional<Integer> amount;

        @Schema(description = "내기 약속장소")
        @Embedded
        private Optional<Location> appointmentLocation;
        public UpdateInfoRequest(BetDateTime betDateTime, Integer amount, Location appointmentLocation) {
            this.betDateTime = Optional.ofNullable(betDateTime);
            this.amount = Optional.ofNullable(amount);
            this.appointmentLocation = Optional.ofNullable(appointmentLocation);
        }
    }
}
