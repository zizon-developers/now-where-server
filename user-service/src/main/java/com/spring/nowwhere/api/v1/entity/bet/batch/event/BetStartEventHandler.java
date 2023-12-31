package com.spring.nowwhere.api.v1.entity.bet.batch.event;//package com.spring.nowwhere.api.v1.batch.event;

import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BetStartEventHandler {
    private final GeoOperations<String, String> geoOperations;

    @Async
    @EventListener
    public void startBet(StartedBetEvent event) throws InterruptedException {
        //내기 시작시간시 처리 logic
        Bet bet = event.getBet();
        Location location = bet.getBetInfo().getAppointmentLocation();
        String destination = location.getLocationName();

        //redis 각각의 키 저장
        String receiverKey = redisNameFormat(bet.getReceiver().getCheckId(), destination);
        String bettorKey = redisNameFormat(bet.getBettor().getCheckId(), destination);
        geoOperations.add(receiverKey, new Point(location.getLongitude(), location.getLatitude()), location.getLocationName());
        geoOperations.add(bettorKey, new Point(location.getLongitude(), location.getLatitude()), location.getLocationName());
    }

    private String redisNameFormat(String user, String destination){
        return String.format("%s:%s", user, destination);
    }
}
