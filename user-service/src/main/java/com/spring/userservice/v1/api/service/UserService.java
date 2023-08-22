package com.spring.userservice.v1.api.service;

import com.spring.userservice.v1.api.auth.OAuthUserDto;
import com.spring.userservice.v1.api.dto.UserDto;
import com.spring.userservice.v1.api.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    OAuthUserDto createUser(OAuthUserDto userDto);
    UserDto getUserByUserId(String userId);
    List<UserDto> getUserByAll();
}