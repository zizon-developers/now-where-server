package com.spring.nowwhere.api.v1.user.service;

import com.spring.nowwhere.api.v1.auth.dto.TokenDto;
import com.spring.nowwhere.api.v1.user.dto.UserDto;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;

import java.util.List;

public interface UserService {
    UserDto getUserByCheckId(String checkId);
    List<UserDto> getUserByAll();

    void logout(String token);

    TokenDto login(OAuthUserDto userDto);

    User updateEmail(OAuthUserDto userDto);

    OAuthUserDto checkAndRegisterUser(OAuthUserDto userDto);

    User reissueWithUserVerification(String email); //

    UserDto updateName(String checkId, String name);

    UserDto updateRemittanceId(String checkId, String remittanceId);
}
