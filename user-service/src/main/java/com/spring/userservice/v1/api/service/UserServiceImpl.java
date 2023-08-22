package com.spring.userservice.v1.api.service;

import com.spring.userservice.v1.api.auth.OAuthUserDto;
import com.spring.userservice.v1.api.auth.exception.DuplicateUserException;
import com.spring.userservice.v1.api.dto.UserDto;
import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.entity.UserRepository;
import com.spring.userservice.v1.api.entity.UserRole;
import com.spring.userservice.v1.api.jwt.TokenProvider;
import com.spring.userservice.v1.api.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.userservice.v1.api.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.userservice.v1.api.security.exception.LogoutTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    //auth
    @Transactional
    public OAuthUserDto createUser(OAuthUserDto userDto) {

        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(ex -> {
                    throw new DuplicateUserException("There is information registered as a member.");
                });

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

    @Override
    public User login(OAuthUserDto userDto) {
        String email = userDto.getEmail();
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("user not found"));

        logoutAccessTokenRedisRepository.findByEmail(email)
                .ifPresent(logoutToken -> logoutAccessTokenRedisRepository.delete(logoutToken));

        return findUser;
    }

    @Transactional
    public LogoutAccessTokenFromRedis logout(String token) {

        String userEmailFromAccessToken = tokenProvider.getUserEmailFromAccessToken(token);

        logoutAccessTokenRedisRepository.findByEmail(userEmailFromAccessToken)
                                        .ifPresent(ex -> {
                                            throw new LogoutTokenException("이미 logout된 token이 있습니다.");
                                        });

        Date expireTimeFromAccessToken = tokenProvider.getExpireTimeFromToken(token);
        LogoutAccessTokenFromRedis logoutAccessToken = LogoutAccessTokenFromRedis.createLogoutAccessToken(token,
                userEmailFromAccessToken, expireTimeFromAccessToken.getTime());

        return logoutAccessTokenRedisRepository.save(logoutAccessToken);
    }

}
