package com.spring.nowwhere.api.v1.user.controller;

import com.spring.nowwhere.api.v1.bet.Bet;
import com.spring.nowwhere.api.v1.bet.BetService;
import com.spring.nowwhere.api.v1.bet.RequestBetDto;
import com.spring.nowwhere.api.v1.user.dto.UserDto;
import com.spring.nowwhere.api.v1.user.dto.UserResponse;
import com.spring.nowwhere.api.v1.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BetService betService;

    @GetMapping("")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "all users", description = "모든 사용자를 조회할 수 있다.")
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<UserDto> userList = userService.getUserByAll();

        List<UserResponse> result = userList.stream()
                                            .map(UserResponse::of)
                                            .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{userId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "get user", description = "특정 사용자를 조회할 수 있다.")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId){
        UserDto findUser = userService.getUserByUserId(userId);
        return ResponseEntity.ok(UserResponse.of(findUser));
    }

    @PostMapping("/{userId}/bets")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "get user", description = "특정 사용자를 조회할 수 있다.")
    public ResponseEntity createBet(@PathVariable String userId,
                                    RequestBetDto requestBetDto){
        Bet bet = betService.createBet(requestBetDto);

        return null;
    }
}
