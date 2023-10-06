package com.spring.nowwhere.api.v1.entity.bet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "내기 시간 정보DTO")
public class BetDateTime {

    @Schema(description = "내기 시작시간")
    private LocalDateTime startTime;
    @Schema(description = "내기 종료시간")
    private LocalDateTime endTime;

    public BetDateTime(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime.withSecond(0).withNano(0);
        this.endTime = endTime.withSecond(0).withNano(0);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetDateTime that = (BetDateTime) o;
        return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }
    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
}
