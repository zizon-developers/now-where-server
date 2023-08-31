package com.spring.nowwhere.api.v1.entity.friend.controller;

import com.spring.nowwhere.api.v1.entity.friend.service.FriendQueryService;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendQueryController {
    private final FriendQueryService friendQueryService;
    private final ResponseApi responseApi;
}
