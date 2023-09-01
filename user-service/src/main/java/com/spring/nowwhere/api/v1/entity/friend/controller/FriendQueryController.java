package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.FriendDto;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendQueryService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendQueryController {
    private final FriendQueryService friendQueryService;
    private final ResponseApi responseApi;

    @GetMapping("/{userId}/friend-request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청목록 가져오기", description = "사용자 친구 요청 목록들을 반환한다.")
    public ResponseEntity<Page<FriendDto>> getFriendRequests(@PathVariable String userId,
                                                             Pageable pageable) {
        Page<FriendDto> friendRequests = friendQueryService.findFriendRequests(userId, pageable);
        return responseApi.success(friendRequests);
    }

    @GetMapping("/{userId}/friends")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청하기", description = "사용자가 특정 사용자에게 친구 추가를 요청한다.")
    public ResponseEntity<Page<FriendDto>> getFriendList(@PathVariable String userId,
                                                         Pageable pageable) {
        Page<FriendDto> friendList = friendQueryService.findFriendList(userId, pageable);
        return responseApi.success(friendList);
    }

}
