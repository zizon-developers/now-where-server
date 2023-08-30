package com.spring.nowwhere.api.v1.entity.user.controller;

import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.user.dto.UserResponse;
import com.spring.nowwhere.api.v1.entity.user.User;
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
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

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

    //내기 횟수, 내기로 번 돈추가하기
    @GetMapping("/me")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "get user", description = "특정 사용자를 조회할 수 있다.")
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request){
        String token = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromAccessToken(token);
        UserDto findUser = userService.getUserBettingInfo(email);
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

    @PostMapping("/{userId}/pay_id")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "update remittanceId", description = "특정 사용자의 송금ID를 변경할 수 있다.")
    public ResponseEntity<UserResponse> updateRemittanceId(@PathVariable("userId") String userId,
                                                   @RequestParam("payId") String payId){
        UserDto findUser = userService.updateRemittanceId(userId, payId);
        return responseApi.success(UserResponse.of(findUser), "송금ID 변경 성공", HttpStatus.OK);
    }
}
