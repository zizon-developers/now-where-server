package com.spring.nowwhere.api.v1.entity.user.service;

import com.spring.nowwhere.api.v1.auth.dto.TokenDto;
import com.spring.nowwhere.api.v1.auth.exception.RefreshTokenNotFoundException;
import com.spring.nowwhere.api.v1.entity.user.dto.UserDto;
import com.spring.nowwhere.api.v1.entity.user.entity.User;
import com.spring.nowwhere.api.v1.entity.user.entity.UserRole;
import com.spring.nowwhere.api.v1.entity.user.exception.DuplicateRemittanceIdException;
import com.spring.nowwhere.api.v1.entity.user.exception.DuplicateUsernameException;
import com.spring.nowwhere.api.v1.redis.refresh.RefreshTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.refresh.RefreshTokenRedisRepository;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
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
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    public UserDto getUserByCheckId(String checkId) {
        User user = userRepository.findByCheckId(checkId)
                .orElseThrow(() -> new UsernameNotFoundException(checkId));

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
    @Transactional
    public TokenDto login(OAuthUserDto userDto) {

        User findUser = userRepository.findByCheckId(userDto.getCheckId()).orElseThrow(
                () -> new UsernameNotFoundException("user not found"));

        String accessToken = tokenProvider.generateJwtAccessToken(findUser);
        String refreshToken = tokenProvider.generateJwtRefreshToken(findUser);


        logoutAccessTokenRedisRepository.findByEmail(userDto.getEmail())
                .ifPresent(logoutToken -> logoutAccessTokenRedisRepository.delete(logoutToken));

        refreshTokenRedisRepository.save(RefreshTokenFromRedis.builder()
                .id(refreshToken)
                .email(findUser.getEmail())
                .expiration(tokenProvider.getExpireTimeFromRefreshToken(refreshToken).getTime())
                .build());

        return new TokenDto(accessToken, refreshToken);
    }

    //친구목록 조회에서 예외 발생시 호출되며 로그인상태라서 따로토큰 제거X 로직도 이게 맞음
    @Override
    @Transactional
    public User updateEmail(OAuthUserDto userDto) {
        User findUser = userRepository.findByCheckId(userDto.getCheckId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if (findUser.isUserIdEmailMatching())
            findUser.updateEmail(userDto.getEmail());

        return findUser;
    }

    @Override
    @Transactional
    public OAuthUserDto checkAndRegisterUser(OAuthUserDto userDto) {
        boolean isRegisterUser = userRepository.findByCheckId(userDto.getCheckId()).isPresent();
        if (isRegisterUser) return userDto;

        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_USER);

        User user = User.builder()
                .checkId(userDto.getCheckId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .profileImg(userDto.getProfileImg())
                .roles(roles)
                .build();
        userRepository.save(user);
        return OAuthUserDto.of(user);
    }

    @Override
    public User reissueWithUserVerification(String email) {

        refreshTokenRedisRepository.findByEmail(email)
                .orElseThrow(() -> new RefreshTokenNotFoundException("refresh token Not Found"));

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        return findUser;
    }

    @Override
    @Transactional
    public UserDto updateName(String checkId, String updateName) {

        User findUser = userRepository.findByCheckId(checkId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        userRepository.findByName(updateName)
                        .ifPresent(ex -> {
                            throw new DuplicateUsernameException(updateName + "은 중복된 이름입니다.");});
        findUser.updateName(updateName);

        return UserDto.of(findUser);
    }

    @Override
    @Transactional
    public UserDto updateRemittanceId(String checkId, String remittanceId) {

        User findUser = userRepository.findByCheckId(checkId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        userRepository.findByRemittanceId(remittanceId)
                        .ifPresent(ex -> {
                            throw new DuplicateRemittanceIdException(remittanceId + "은 중복된 송금ID입니다.");});
        findUser.updateRemittanceId(remittanceId);

        return UserDto.of(findUser);
    }

    @Transactional
    public void logout(String token) {

        String findEmail = tokenProvider.getUserEmailFromAccessToken(token);

        logoutAccessTokenRedisRepository.findByEmail(findEmail).ifPresent(ex -> {
                                            throw new LogoutTokenException("이미 logout된 token이 있습니다.");});

        Date expireTimeFromAccessToken = tokenProvider.getExpireTimeFromAccessToken(token);
        LogoutAccessTokenFromRedis logoutAccessToken = LogoutAccessTokenFromRedis.createLogoutAccessToken(token,
                findEmail, expireTimeFromAccessToken.getTime());

        refreshTokenRedisRepository.findByEmail(findEmail)
                .ifPresent(refreshToken -> refreshTokenRedisRepository.delete(refreshToken));

        logoutAccessTokenRedisRepository.save(logoutAccessToken);
    }
}
