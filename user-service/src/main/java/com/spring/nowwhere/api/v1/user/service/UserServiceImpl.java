package com.spring.nowwhere.api.v1.user.service;

import com.spring.nowwhere.api.v1.user.dto.UserDto;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.entity.UserRole;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        User findUser = userRepository.findByUserId(userDto.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException("user not found"));

        logoutAccessTokenRedisRepository.findByEmail(userDto.getEmail())
                .ifPresent(logoutToken -> logoutAccessTokenRedisRepository.delete(logoutToken));
        return findUser;
    }

    //친구목록 조회에서 예외 발생시 호출되며 로그인상태라서 따로토큰 제거X 로직도 이게 맞음
    @Override
    @Transactional
    public User updateEmail(OAuthUserDto userDto) {
        User findUser = userRepository.findByUserId(userDto.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if (findUser.isUserIdEmailMatching())
            findUser.updateEmail(userDto.getEmail());

        return findUser;
    }

    @Override
    @Transactional
    public OAuthUserDto checkAndRegisterUser(OAuthUserDto userDto) {
        boolean isRegisterUser = userRepository.findByUserId(userDto.getUserId()).isPresent();
        if (isRegisterUser) return userDto;

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

    @Transactional
    public LogoutAccessTokenFromRedis logout(String token) {

        String userEmailFromAccessToken = tokenProvider.getUserEmailFromAccessToken(token);

        logoutAccessTokenRedisRepository.findByEmail(userEmailFromAccessToken).ifPresent(ex -> {
                                            throw new LogoutTokenException("이미 logout된 token이 있습니다.");
                                        });

        Date expireTimeFromAccessToken = tokenProvider.getExpireTimeFromToken(token);
        LogoutAccessTokenFromRedis logoutAccessToken = LogoutAccessTokenFromRedis.createLogoutAccessToken(token,
                userEmailFromAccessToken, expireTimeFromAccessToken.getTime());

        return logoutAccessTokenRedisRepository.save(logoutAccessToken);
    }
}
