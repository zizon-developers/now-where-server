package com.spring.nowwhere.api.v1.entity.user.controller;

//import com.spring.nowwhere.api.v1.bet.BetService;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.user.dto.UserResponse;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.entity.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
//    private final BetService betService;
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

    @PostMapping("/logout")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "logout", description = "로그인을 성공한 사용자는 로그아웃을 할 수 있다.(refresh token도 삭제)")
    public ResponseEntity<UserResponse> logout(HttpServletRequest request){

        String token = getTokenByReqeust(request);
        userService.logout(token);
        return responseApi.success("logout 되었습니다.");
    }

    @GetMapping("/reissue")
    @Operation(security = { @SecurityRequirement(name = "bearer-key (refresh token)") },
            summary = "reissue", description = "refresh token을 이용해서 access token을 재발행 가능하다. (user정보 넘겨줄 수 있는지 FE랑 이야기)")
    public ResponseEntity<UserResponse> reissue(HttpServletRequest request,
                                                HttpServletResponse response){

        String refreshToken = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromRefreshToken(refreshToken);

        User user = userService.reissueWithUserVerification(email);
        String accessToken = tokenProvider.generateJwtAccessToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);

        return responseApi.success(UserResponse.of(user), "access token 재발행 성공", HttpStatus.OK);
    }

    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }

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
    public ResponseEntity<UserResponse> getUser(@PathVariable String checkId){
        UserDto findUser = userService.getUserByCheckId(checkId);
        return ResponseEntity.ok(UserResponse.of(findUser));
    }
    @PostMapping("/{userId}/name")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "update name", description = "특정 사용자를 이름을 변경할 수 있다.")
    public ResponseEntity<UserResponse> updateName(@PathVariable("userId") String userId,
                                                   @RequestParam("name") String name){
        UserDto findUser = userService.updateName(userId, name);
        return responseApi.success(UserResponse.of(findUser), "닉네임 변경 성공", HttpStatus.OK);
    }

    @PostMapping("/{userId}/pay")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "update remittanceId", description = "특정 사용자의 송금ID를 변경할 수 있다.")
    public ResponseEntity<UserResponse> updateRemittanceId(@PathVariable("userId") String userId,
                                                   @RequestParam("payId") String payId){
        UserDto findUser = userService.updateRemittanceId(userId, payId);
        return responseApi.success(UserResponse.of(findUser), "송금ID 변경 성공", HttpStatus.OK);
    }

//    @PostMapping("/{userId}/bets")
//    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
//            summary = "create bet", description = "특정 사용자에게 내기를 요청할 수 있다.")
//    public ResponseEntity<ResponseBet> createBet(@PathVariable String userId,
//                                                 RequestBet requestBet){
//
//        ResponseBet responseBet = betService.createBet(userId, requestBet);
//        return responseApi.success(responseBet, "내기 저장에 성공했습니다.", HttpStatus.CREATED);
//    }
}
