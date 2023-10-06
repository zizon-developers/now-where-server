package com.spring.nowwhere.api.v1.batch;//package com.spring.nowwhere.api.v1.batch;

import com.spring.nowwhere.api.v1.batch.event.StartedBetEvent;
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

    @Scheduled(cron = "0 0/10 * * * *", zone = "Asia/Seoul") //매일 10분마다 처리하기
    public void betStartTimeProcess() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        repository.findBetsByStartTime(now).stream()
                .map(StartedBetEvent::new)
                .forEach(publisher::publishEvent);
    }

}
