package com.spring.nowwhere.api.v1.controller;

import com.spring.nowwhere.api.v1.dto.UserDto;
import com.spring.nowwhere.api.v1.vo.UserResponse;
import com.spring.nowwhere.api.v1.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;

    @Timed(value = "users.status", longTask = true)
    public String status(){
        return String.format("It's a Working in User Service"
                +  ", port(local.server.port) =" + env.getProperty("local.server.port")
                +  ", port(server.port) =" + env.getProperty("server.port")
                +  ", token secret =" + env.getProperty("token.secret")
                +  ", token expiration time =" + env.getProperty("token.expiration_time"));
    }

    //page 쿼리로 수정하기
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
}
