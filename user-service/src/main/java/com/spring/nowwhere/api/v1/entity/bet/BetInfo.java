package com.spring.nowwhere.api.v1.entity.bet;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BetInfo {
    private LocalDateTime startTime; //시작 시간
    private LocalDateTime endTime; //종료 시간
    private int amount;
    @Embedded
    private Location appointmentLocation; //약속 장소

    @Builder
    private BetInfo(LocalDateTime startTime, LocalDateTime endTime, int amount, Location appointmentLocation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.appointmentLocation = appointmentLocation;
    }
}
