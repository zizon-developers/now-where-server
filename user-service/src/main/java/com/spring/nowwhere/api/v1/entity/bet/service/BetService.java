package com.spring.nowwhere.api.v1.entity.bet.service;

import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.dto.*;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetStatusException;
import com.spring.nowwhere.api.v1.entity.bet.exception.TimeValidationException;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static com.spring.nowwhere.api.v1.entity.bet.BetStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BetService {

    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final int BETTOR_INDEX = 0;
    private final int RECEIVER_INDEX = 1;

    public void acceptBet(String receiverId, AcceptBetRequest acceptBetRequest) {
        String bettorId = acceptBetRequest.getBettorId();
        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, receiverId);
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetDateTime betDateTime = acceptBetRequest.getBetDateTime();
        Bet bet = getBetByDateTimeAndRequest(bettor, receiver, betDateTime);
        bet.updateBetStatus(WAITING);
    }
    public void rejectBet(String receiverId, RejectBetRequest rejectBetRequest){
        String bettorId = rejectBetRequest.getBettorId();
        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, receiverId);
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetDateTime betDateTime = rejectBetRequest.getBetDateTime();
        Bet bet = getBetByDateTimeAndRequest(bettor, receiver, betDateTime);

        betRepository.delete(bet);
    }
    public void removeBet(String bettorId, RemoveBetRequest removeBetRequest){
        String receiverId = removeBetRequest.getReceiverId();
        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, receiverId);
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetDateTime betDateTime = removeBetRequest.getBetDateTime();
        Bet bet = getBetByDateTimeAndCheckStatus(bettor, receiver, betDateTime);

        betRepository.delete(bet);
    }

    public void updateBetInfo(String bettorId, UpdateBetRequest updateBetRequest){
        String receiverId = updateBetRequest.getReceiverId();
        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, receiverId);
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetDateTime betDateTime = updateBetRequest.getBetDateTime();

        Bet bet = getBetByDateTimeAndCheckStatus(bettor, receiver, betDateTime);
        BetInfo updateBetInfo = getUpdateBetInfo(updateBetRequest, bet.getBetInfo());

        bet.updateBetInfo(updateBetInfo);
        bet.updateBetStatus(REQUESTED);
    }

    private Bet getBetByDateTimeAndCheckStatus(User bettor, User receiver, BetDateTime betDateTime) {
        Bet bet = betRepository.findBetsInTimeRange(bettor, receiver, betDateTime)
                .orElseThrow(() -> new BetNotFoundException("해당하는 내기 정보가 없습니다."));
        BetStatus betStatus = bet.getBetStatus();
        if (betStatus.equals(COMPLETED) || betStatus.equals(IN_PROGRESS)){
            throw new BetStatusException("이미 내기가 진행중이거나, 완료되었습니다.");
        }
        return bet;
    }

    private Bet getBetByDateTimeAndRequest(User bettor, User receiver, BetDateTime betDateTime) {
        Bet bet = betRepository.findBetsInTimeRange(bettor, receiver, betDateTime)
                .orElseThrow(() -> new BetNotFoundException("해당하는 내기 정보가 없습니다."));
        BetStatus betStatus = bet.getBetStatus();
        if (!REQUESTED.equals(betStatus)){
            throw new BetStatusException("내기의 상태가 요청상태가 아닙니다.");
        }
        return bet;
    }

    private static BetInfo getUpdateBetInfo(UpdateBetRequest updateBetRequest, BetInfo betInfo) {
        UpdateBetRequest.UpdateInfoRequest updateBetInfoRequest =
                                                    updateBetRequest.getUpdateBetInfoRequest();

        BetDateTime updateBetDateTime = updateBetInfoRequest.getBetDateTime()
                                                            .orElse(betInfo.getBetDateTime());
        int updateAmount = updateBetInfoRequest.getAmount()
                                                .orElse(betInfo.getAmount());
        Location updateLocation = updateBetInfoRequest.getAppointmentLocation()
                                                      .orElse(betInfo.getAppointmentLocation());

        return BetInfo.builder()
                .betDateTime(updateBetDateTime)
                .amount(updateAmount)
                .appointmentLocation(updateLocation)
                .build();
    }

    public ResponseBet createBet(String bettorId, RequestBet requestBet) {

        List<User> bettorAndReceiver = checkBettorAndReceiver(bettorId, requestBet.getReceiverId());
        User bettor = bettorAndReceiver.get(BETTOR_INDEX);
        User receiver = bettorAndReceiver.get(RECEIVER_INDEX);

        BetInfo betInfo = requestBet.getBetInfo();
        BetDateTime betDateTime = betInfo.getBetDateTime();

        validateTimeRange(betDateTime);
        validationTimeRange(bettor, receiver, betDateTime);

        return ResponseBet.of(saveBet(bettor, receiver, betInfo));
    }

    private Bet saveBet(User bettor, User receiver, BetInfo betInfo) {
        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(REQUESTED)
                .betInfo(betInfo)
                .build();
        return betRepository.save(bet);
    }

    private List<User> checkBettorAndReceiver(String bettorId, String receiverId) {
        List<User> bettorAndReceiver = userRepository
                .findSenderAndReceiver(bettorId, receiverId);
        if (bettorAndReceiver.size() != 2)
            throw new UsernameNotFoundException("bettor and receiver is not found");
        return bettorAndReceiver;
    }

    private void validationTimeRange(User bettor, User receiver, BetDateTime betDateTime) {
        betRepository.findBetsInTimeRange(bettor, receiver, betDateTime)
                .ifPresent((ex)->{
                    throw new TimeValidationException("이미 시간에 포함된 내기가 있습니다.");});
    }

    private void validateTimeRange(BetDateTime betDateTime){
        Duration duration = Duration.between(betDateTime.getStartTime(), betDateTime.getEndTime())
                                    .minusMinutes(5);
        if (duration.toMinutes() < 0)
            throw new TimeValidationException("내기의 시작시간과 끝나는 시간의 차이는 5분 이상이어야 합니다.");
    }
}
