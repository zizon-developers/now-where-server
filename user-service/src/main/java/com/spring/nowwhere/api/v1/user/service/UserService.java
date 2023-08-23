package com.spring.nowwhere.api.v1.user.service;

import com.spring.nowwhere.api.v1.user.dto.UserDto;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;

import java.util.List;

public interface UserService {
    UserDto getUserByUserId(String userId);
    List<UserDto> getUserByAll();

    LogoutAccessTokenFromRedis logout(String token);

    User login(OAuthUserDto userDto);

    User updateEmail(OAuthUserDto userDto);

    OAuthUserDto checkAndRegisterUser(OAuthUserDto userDto);
}
