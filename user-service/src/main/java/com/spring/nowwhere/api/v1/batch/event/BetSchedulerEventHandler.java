package com.spring.nowwhere.api.v1.batch.event;//package com.spring.nowwhere.api.v1.batch.event;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetInfo;
import com.spring.nowwhere.api.v1.entity.bet.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BetSchedulerEventHandler {
    private final GeoOperations<String, String> geoOperations;

    @Async
    @EventListener
    public void startBet(StartedBetEvent event) throws InterruptedException {
        //내기 시작시간시 처리 logic
        Bet bet = event.getBet();
        BetInfo betInfo = bet.getBetInfo();
        Location appointmentLocation = betInfo.getAppointmentLocation();
//        appointmentLocation

    }

    @Async
    @EventListener
    public void endBet(EndedBetEvent event) throws InterruptedException {
        //내기 종료시간시 처리 logic
        Bet bet = event.getBet();
    }
//    private String KeyFormat(String user, String ){
//        String.format("%s:%s",)
//    }

}
