package com.spring.nowwhere.api.v1.entity.bet.batch;

import com.spring.nowwhere.api.v1.entity.bet.batch.event.StartedBetEvent;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BetSchedulerService {

    private final ApplicationEventPublisher publisher;
    private final BetRepository repository;

    //매일 10분마다 처리하기
    @Scheduled(cron = "0 0/10 * * * *", zone = "Asia/Seoul")
    public void betStartTimeProcess() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).withSecond(0).withNano(0);
        repository.findBetsByStartTime(now).stream()
                .map(StartedBetEvent::new)
                .forEach(publisher::publishEvent);
    }
}
