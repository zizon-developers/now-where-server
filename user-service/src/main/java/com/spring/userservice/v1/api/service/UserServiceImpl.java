package com.spring.userservice.v1.api.service;

import com.spring.userservice.v1.api.auth.OAuthUserDto;
import com.spring.userservice.v1.api.auth.exception.DuplicateUserException;
import com.spring.userservice.v1.api.dto.UserDto;
import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.entity.UserRepository;
import com.spring.userservice.v1.api.entity.UserRole;
import com.spring.userservice.v1.api.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //auth
    public OAuthUserDto createUser(OAuthUserDto userDto) {
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_USER);

        User user = User.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .roles(roles)
                .build();
        userRepository.save(user);

        return OAuthUserDto.of(user);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        return UserDto.of(user);
    }

    @Override
    public List<UserDto> getUserByAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDto::of)
                .collect(Collectors.toList());
    }


}
