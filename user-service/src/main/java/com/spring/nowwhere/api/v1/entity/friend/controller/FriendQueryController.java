package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.dto.ResponseFriendDto;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendQueryController {
    private final ResponseApi responseApi;
    private final TokenProvider tokenProvider;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @GetMapping("/friend-request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 요청목록 가져오기", description = "사용자 친구 요청 목록들을 반환한다.")
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(
//                                    mediaType = "application/json",
//                                    schema = @Schema(implementation = ResponseFriendDto.class)))
//    })
    public ResponseEntity<Page<ResponseFriendDto>> getFriendRequests(HttpServletRequest request,
                                                                     Pageable pageable) {

        String token = getTokenByReqeust(request);
        User sender = getUserFromAccessToken(token);
        Page<ResponseFriendDto> requestFriendPage = friendRepository
                .findBySenderAndFriendStatus(sender, FriendStatus.PENDING, pageable)
                .map(ResponseFriendDto::of);
        return responseApi.success(requestFriendPage);
    }

    @GetMapping("/friends")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "친구 목록 가져오기", description = "사용자의 친구 목록들을 반환한다.")
    public ResponseEntity<Page<ResponseFriendDto>> getFriendList(HttpServletRequest request,
                                                                 Pageable pageable) {
        String token = getTokenByReqeust(request);
        User sender = getUserFromAccessToken(token);
        Page<ResponseFriendDto> friendList = friendRepository
                                .findBySenderAndFriendStatus(sender, FriendStatus.PENDING, pageable)
                                .map(ResponseFriendDto::of);
        return responseApi.success(friendList);
    }

    private User getUserFromAccessToken(String token) {
        String checkId = tokenProvider.getCheckIdFromAccessToken(token);
        return userRepository.findByCheckId(checkId)
                .orElseThrow(() -> new UsernameNotFoundException(checkId + "에 대한 유저가 존재하지 않습니다."));
    }
    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }

}
