package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.dto.FriendDto;
import com.spring.nowwhere.api.v1.entity.friend.dto.ResponseFriend;
import com.spring.nowwhere.api.v1.entity.friend.dto.ResponseFriendRequest;
import com.spring.nowwhere.api.v1.entity.friend.service.FriendQueryService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendQueryController {
    private final FriendQueryService friendQueryService;
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;

    @GetMapping("/friend-request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청목록 가져오기", description = "사용자 친구 요청 목록들을 반환한다.")
    public ResponseEntity<Page<ResponseFriendRequest>> getFriendRequests(HttpServletRequest request,
                                                                         Pageable pageable) {

        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        Page<ResponseFriendRequest> friendRequests = friendQueryService.findFriendRequests(checkId, pageable)
                .map(ResponseFriendRequest::new);
        return responseApi.success(friendRequests);
    }

    @GetMapping("/friends")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 목록 가져오기", description = "사용자의 친구 목록들을 반환한다.")
    public ResponseEntity<Page<ResponseFriend>> getFriendList(HttpServletRequest request,
                                                              Pageable pageable) {
        String token = getTokenByReqeust(request);
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        Page<ResponseFriend> friendList = friendQueryService.findFriendList(checkId, pageable)
                .map(ResponseFriend::new);
        return responseApi.success(friendList);
    }
    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }

}
