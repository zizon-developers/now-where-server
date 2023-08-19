package com.spring.userservice.service;

import com.spring.userservice.dto.UserDto;
import com.spring.userservice.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    UserDto getUserDetailsByEmail(String username);

    List<UserDto> getUserByAll();
}
