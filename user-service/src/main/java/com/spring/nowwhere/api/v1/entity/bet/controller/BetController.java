package com.spring.nowwhere.api.v1.entity.bet.controller;

import com.spring.nowwhere.api.v1.entity.bet.service.BetService;
import com.spring.nowwhere.api.v1.entity.bet.dto.RequestBet;
import com.spring.nowwhere.api.v1.entity.bet.dto.ResponseBet;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BetController {
    private final BetService betService;
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

    @PostMapping("/bets")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "create bet", description = "특정 사용자에게 내기를 요청할 수 있다.")
    public ResponseEntity<ResponseBet> createBet(HttpServletRequest request,
                                                 @RequestBody RequestBet requestBet){
        String token = getTokenByRequest(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        ResponseBet responseBet = betService.createBet(checkId, requestBet);
        return responseApi.success(responseBet, "내기 요청에 성공했습니다.", HttpStatus.CREATED);
    }
    private static String getTokenByRequest(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }
}
