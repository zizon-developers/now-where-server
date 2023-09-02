package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.dto.RequestFriend;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

    @PostMapping("/friend-request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청하기", description = "사용자가 특정 사용자에게 친구 추가를 요청한다.")
    public ResponseEntity sendFriendRequest(HttpServletRequest request,
                                            @RequestBody RequestFriend requestFriend) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        friendService.createFriendRequest(checkId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 성공");
    }

    @PutMapping("/friend-request/accept")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 수락하기", description = "특정 사용자의 친구 추가를 요청을 수락한다.")
    public ResponseEntity acceptFriendRequest(HttpServletRequest request,
                                              @RequestBody RequestFriend requestFriend) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        friendService.updateFriendRequestToAccept(checkId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 수락 성공");
    }

    @PutMapping("/friend-request/reject")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 거절하기", description = "특정 사용자의 친구 추가를 요청을 거절한다.")
    public ResponseEntity rejectFriendRequest(HttpServletRequest request,
                                              @RequestBody RequestFriend requestFriend) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        friendService.updateFriendRequestToReject(checkId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 거절 성공");
    }

    @PutMapping("/friend-request/cancel")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청 취소하기", description = "특정 사용자의 친구 추가 요청을 취소한다.")
    public ResponseEntity cancelFriendRequest(HttpServletRequest request,
                                              @RequestBody RequestFriend requestFriend) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        friendService.updateFriendRequestToCancel(checkId, requestFriend.getReceiverId());
        return responseApi.success("친구요청 취소 성공");
    }

    @DeleteMapping("/friends")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 삭제하기", description = "특정 사용자의 친구를 삭제한다.")
    public ResponseEntity removeFriend(HttpServletRequest request,
                                       @RequestBody RequestFriend requestFriend) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        friendService.removeFriend(checkId, requestFriend.getReceiverId());
        return responseApi.success("친구 삭제 성공");
    }

    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }
}
