package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final ResponseApi responseApi;

    @PostMapping("/{userId}/friends")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "친구 요청하기", description = "사용자가 특정 사용자에게 친구 추가를 요청할 수 있다.")
    public ResponseEntity sendFriendRequest (@PathVariable("userId") String userId){
        friendService.createFriendRequest();
    }
}
