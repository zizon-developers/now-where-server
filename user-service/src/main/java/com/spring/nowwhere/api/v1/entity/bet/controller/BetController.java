package com.spring.nowwhere.api.v1.entity.bet.controller;

import com.spring.nowwhere.api.v1.entity.bet.dto.*;
import com.spring.nowwhere.api.v1.entity.bet.service.BetService;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BetController {
    private final BetService betService;
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

    @PutMapping("/bets/accept")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "accept bet", description = "특정 사용자의 내기 요청을 수락한다.")
    public ResponseEntity acceptBet(HttpServletRequest request,
                                                 @RequestBody AcceptBetRequest acceptBetRequest){
        String checkId = getCheckIdByRequest(request);

        betService.acceptBet(checkId, acceptBetRequest);
        return responseApi.success("내기가 수락 되었습니다.");
    }

    @DeleteMapping("/bets/reject")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "reject bet", description = "거절한 내기는 삭제됩니다.")
    public ResponseEntity rejectBet(HttpServletRequest request,
                                                 @RequestBody RejectBetRequest rejectBetRequest){
        String checkId = getCheckIdByRequest(request);
        betService.rejectBet(checkId, rejectBetRequest);
        return responseApi.success("내기가 거절 되었습니다.");
    }

    @DeleteMapping("/bets/cancel")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "cancel bet", description = "취소한 내기는 삭제됩니다.")
    public ResponseEntity cancelBet(HttpServletRequest request,
                                                 @RequestBody RemoveBetRequest removeBetRequest){
        String checkId = getCheckIdByRequest(request);
        betService.removeBet(checkId, removeBetRequest);
        return responseApi.success("내기가 취소 되었습니다.");
    }

    @PostMapping("/bets")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "create bet", description = "특정 사용자에게 내기를 요청할 수 있다.")
    public ResponseEntity<ResponseBet> createBet(HttpServletRequest request,
                                                 @RequestBody RequestBet requestBet){
        String checkId = getCheckIdByRequest(request);
        ResponseBet responseBet = betService.createBet(checkId, requestBet);
        return responseApi.success(responseBet, "내기 요청에 성공했습니다.", HttpStatus.CREATED);
    }

    @PutMapping("/bets/info")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "update betInfo", description = "사용자 내기 정보 수정를 수정할 수 있다.")
    public ResponseEntity<ResponseBet> updateBetInfo(HttpServletRequest request,
                                                 @RequestBody UpdateBetRequest updateBetRequest){
        String checkId = getCheckIdByRequest(request);
        betService.updateBetInfo(checkId, updateBetRequest);
        return responseApi.success("상대방 수락시 내기정보가 변경됩니다.");
    }

    private String getCheckIdByRequest(HttpServletRequest request) {
        String token = getTokenByRequest(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        return checkId;
    }
    private String getTokenByRequest(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }
}
