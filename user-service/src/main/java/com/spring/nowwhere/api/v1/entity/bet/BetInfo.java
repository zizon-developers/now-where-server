package com.spring.nowwhere.api.v1.entity.bet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetInfo {
    @Schema(description = "내기 시작시간")
    private LocalDateTime startTime;
    @Schema(description = "내기 종료시간")
    private LocalDateTime endTime;
    @Schema(description = "내기 금액", minimum = "0")
    private int amount;
    @Schema(description = "내기 약속장소")
    @Embedded
    private Location appointmentLocation;

    @Builder
    private BetInfo(LocalDateTime startTime, LocalDateTime endTime, int amount, Location appointmentLocation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.appointmentLocation = appointmentLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetInfo betInfo = (BetInfo) o;
        return amount == betInfo.amount && Objects.equals(startTime, betInfo.startTime) && Objects.equals(endTime, betInfo.endTime) && Objects.equals(appointmentLocation, betInfo.appointmentLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, amount, appointmentLocation);
    }
}
