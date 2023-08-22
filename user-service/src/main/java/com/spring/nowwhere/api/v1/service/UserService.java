package com.spring.nowwhere.api.v1.service;

import com.spring.nowwhere.api.v1.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.User;
import com.spring.nowwhere.api.v1.auth.OAuthUserDto;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;

import java.util.List;

public interface UserService {
    OAuthUserDto createUser(OAuthUserDto userDto);
    UserDto getUserByUserId(String userId);
    List<UserDto> getUserByAll();

    LogoutAccessTokenFromRedis logout(String token);

    User login(OAuthUserDto userDto);

    void updateEmail(OAuthUserDto userDto);
}
