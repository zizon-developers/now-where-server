package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.dto.RequestFriend;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final ResponseApi responseApi;

    @PostMapping("/{userId}/friend-request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청하기", description = "사용자가 특정 사용자에게 친구 추가를 요청한다.")
    public ResponseEntity sendFriendRequest(@PathVariable("userId") String userId,
                                            @RequestBody RequestFriend requestFriend) {

        friendService.createFriendRequest(userId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 성공");
    }

    @PutMapping("/{userId}/friend-request/accept")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 수락하기", description = "특정 사용자의 친구 추가를 요청을 수락한다.")
    public ResponseEntity acceptFriendRequest(@PathVariable("userId") String userId,
                                            @RequestBody RequestFriend requestFriend) {

        friendService.updateFriendRequestToAccept(userId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 수락 성공");
    }
    @PutMapping("/{userId}/friend-request/reject")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 거절하기", description = "특정 사용자의 친구 추가를 요청을 거절한다.")
    public ResponseEntity rejectFriendRequest(@PathVariable("userId") String userId,
                                            @RequestBody RequestFriend requestFriend) {

        friendService.updateFriendRequestToReject(userId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 거절 성공");
    }
    @PutMapping("/{userId}/friend-request/cancel")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 취소하기", description = "특정 사용자의 친구 추가 요청을 취소한다.")
    public ResponseEntity cancelFriendRequest(@PathVariable("userId") String userId,
                                            @RequestBody RequestFriend requestFriend) {

        friendService.updateFriendRequestToCancel(userId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 취소 성공");
    }
}
