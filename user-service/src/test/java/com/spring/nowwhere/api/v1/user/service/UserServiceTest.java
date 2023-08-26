package com.spring.nowwhere.api.v1.user.service;

import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.auth.exception.RefreshTokenNotFoundException;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.nowwhere.api.v1.redis.refresh.RefreshTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.refresh.RefreshTokenRedisRepository;
import com.spring.nowwhere.api.v1.security.exception.LogoutTokenException;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.exception.DuplicateRemittanceIdException;
import com.spring.nowwhere.api.v1.user.exception.DuplicateUsernameException;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Autowired
    private LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    @Autowired
    private TokenProvider tokenProvider;

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
        refreshTokenRedisRepository.deleteAll();
        logoutAccessTokenRedisRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자의 이메일과 checkId가 같은경우 email을 변경할 수 있다.")
    public void updateEmail() {
        // given
        User user = User.builder()
                .checkId("same")
                .email("same")
                .name("admin")
                .password("password")
                .build();
        userRepository.save(user);
        // when
        userService.updateEmail(OAuthUserDto.builder()
                .checkId("same")
                .email("change")
                .build());

        Optional<User> findUser = userRepository.findByEmail("change");
        // then
        Assertions.assertEquals(findUser.get().getEmail(),"change");
    }

    @Test
    @DisplayName("유저 정보가 DB에 없을 경우 DB에 저장된다.")
    public void checkAndRegisterUser() {
        // when
        OAuthUserDto oAuthUserDto = userService.checkAndRegisterUser(OAuthUserDto.builder()
                .checkId("testId")
                .email("test@naver.com")
                .name("test").build());

        User user = userRepository.findByCheckId(oAuthUserDto.getCheckId()).get();
        // then
        assertThat(oAuthUserDto.getCheckId()).isEqualTo(user.getCheckId());
        assertThat(oAuthUserDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(oAuthUserDto.getName()).isEqualTo(user.getName());
    }

    @DisplayName("사용자 로그인 시나리오")
    @TestFactory
    Collection<DynamicTest> loginDynamicTest() {
        // given
        return List.of(
                DynamicTest.dynamicTest("사용자는 로그인할 수 있다.",()->{
                    //given
                    String email = "email";
                    User user = User.builder()
                            .checkId("userId")
                            .email(email)
                            .name("name").build();
                    userRepository.save(user);

                    LogoutAccessTokenFromRedis logoutAccessTokenFromRedis =
                            logoutAccessTokenRedisRepository.save(LogoutAccessTokenFromRedis
                                    .createLogoutAccessToken("accessToken", email, 100000L));
                    // when
                    userService.login(OAuthUserDto.of(user));
                    // then
                    Optional<RefreshTokenFromRedis> refreshToken = refreshTokenRedisRepository.findByEmail(email);
                    Optional<LogoutAccessTokenFromRedis> findLogoutToken
                            = logoutAccessTokenRedisRepository.findByEmail(email);

                    assertThat(findLogoutToken.isEmpty()).isTrue();
                    assertThat(refreshToken.isPresent()).isTrue();
                    assertThat(refreshToken.get().getEmail()).isEqualTo(user.getEmail());
                }),
                DynamicTest.dynamicTest("DB에 user정보가 없다면 로그인할 수 없다.",()->{
                    //when //then
                    assertThatThrownBy(() -> userService.login(OAuthUserDto.of(User.builder().email("ex").build())))
                            .isInstanceOf(UsernameNotFoundException.class)
                            .hasMessage("user not found");
                })
        );
    }
    
    @DisplayName("사용자 로그아웃 시나리오")
    @TestFactory
    Collection<DynamicTest> logoutDynamicTest() {
        // given
        String email = "email";

        User user = User.builder()
                .checkId("userId")
                .email(email)
                .name("name").build();
        userRepository.save(user);

        return List.of(
                DynamicTest.dynamicTest("사용자는 로그아웃할 수 있다.",()->{
                    //given
                    RefreshTokenFromRedis refreshTokenFromRedis = RefreshTokenFromRedis
                            .createRefreshToken("refreshToken", email, 1000000L);
                    refreshTokenRedisRepository.save(refreshTokenFromRedis);

                    //when
                    String token = tokenProvider.generateJwtAccessToken(user);
                    userService.logout(token);

                    // then
                    Optional<RefreshTokenFromRedis> refreshToken =
                            refreshTokenRedisRepository.findByEmail(email);
                    Optional<LogoutAccessTokenFromRedis> logoutAccessToken =
                            logoutAccessTokenRedisRepository.findByEmail(email);

                    assertThat(logoutAccessToken.isPresent()).isTrue();
                    assertThat(refreshToken.isEmpty()).isTrue();
                    assertThat(logoutAccessToken.get().getEmail()).isEqualTo(user.getEmail());

                }),
                DynamicTest.dynamicTest("Redis에 로그아웃 정보가 있으면 로그아웃을 재 시도할 수 없다.",()->{
                    //given
                    String token = tokenProvider.generateJwtAccessToken(user);
                    logoutAccessTokenRedisRepository.save(LogoutAccessTokenFromRedis
                            .builder().id(token)
                            .email(email)
                            .expiration(1000000L).build());
                    //when //then
                    assertThatThrownBy(() -> userService.logout(token))
                            .isInstanceOf(LogoutTokenException.class)
                            .hasMessage("이미 logout된 token이 있습니다.");
                })
        );
    }

    @DisplayName("refresh 토큰을 받기전 사용자 시나리오")
    @TestFactory
    Collection<DynamicTest> reissueWithUserVerification() {
        // given
        String email = "email";
        User user = User.builder()
                .checkId("userId")
                .email(email)
                .name("name").build();
        userRepository.save(user);

        return List.of(
                DynamicTest.dynamicTest("회원이 로그인에 성공한 경우 검증에 성공한다.", () -> {
                    //given
                    userService.login(OAuthUserDto.of(user));
                    //when
                    User findUser = userService.reissueWithUserVerification(email);
                    //then
                    assertAll(
                            () -> assertEquals(user.getCheckId(), findUser.getCheckId()),
                            () -> assertEquals(user.getEmail(), findUser.getEmail()),
                            () -> assertEquals(user.getName(), findUser.getName())
                    );
                }),
                DynamicTest.dynamicTest("refresh token이 만료된 유저는 검증에 실패한다.", () -> {
                    //given
                    refreshTokenRedisRepository.findByEmail(email)
                            .ifPresent(token -> refreshTokenRedisRepository.delete(token));
                    //when //then
                    assertThatThrownBy(() -> userService.reissueWithUserVerification(email))
                            .isInstanceOf(RefreshTokenNotFoundException.class)
                            .hasMessage("refresh token Not Found");

                })
        );
    }

    @DisplayName("사용자 닉네임 변경 시나리오")
    @TestFactory
    Collection<DynamicTest> updateName() {
        // given
        User user1 = User.builder()
                .checkId("test")
                .email("test@test.com")
                .name("test")
                .build();

        User user2 = User.builder()
                .checkId("exception")
                .email("exception@test.com")
                .name("ex").build();

        userRepository.saveAll(List.of(user1,user2));

        return List.of(
                DynamicTest.dynamicTest("사용자는 닉네임을 변경할 수 있다.", () -> {
                    //when
                    String updateName = "updateName";
                    userService.updateName(user1.getCheckId(), updateName);
                    //then
                    User findUser = userRepository.findByName(updateName).get();
                    assertAll(
                            () -> assertEquals(findUser.getCheckId(), user1.getCheckId()),
                            () -> assertEquals(findUser.getEmail(), user1.getEmail()),
                            () -> assertEquals(findUser.getName(), updateName)
                    );
                }),
                DynamicTest.dynamicTest("변경할 이름이 중복된 경우 예외가 발생한다.", () -> {
                    //when //then
                    String updateName = "ex";
                    assertThatThrownBy(() -> userService.updateName(user1.getCheckId(),updateName))
                            .isInstanceOf(DuplicateUsernameException.class)
                            .hasMessage(updateName + "은 중복된 이름입니다.");
                })
        );
    }
    @DisplayName("사용자 송금ID 변경 시나리오")
    @TestFactory
    Collection<DynamicTest> updateRemittanceId() {
        // given
        User user1 = User.builder()
                .checkId("test")
                .email("test@test.com")
                .name("test")
                .remittanceId(null)
                .build();

        User user2 = User.builder()
                .checkId("exceptionId")
                .email("exception@test.com")
                .name("exception")
                .remittanceId("ex")
                .build();

        userRepository.saveAll(List.of(user1,user2));

        return List.of(
                DynamicTest.dynamicTest("사용자는 송금ID 변경할 수 있다.", () -> {
                    //when
                    String updateRemittanceId = "remittanceId";
                    userService.updateRemittanceId(user1.getCheckId(), updateRemittanceId);
                    //then
                    User findUser = userRepository.findByName(updateRemittanceId).get();
                    assertAll(
                            () -> assertEquals(findUser.getCheckId(), user1.getCheckId()),
                            () -> assertEquals(findUser.getEmail(), user1.getEmail()),
                            () -> assertEquals(findUser.getName(), updateRemittanceId)
                    );
                }),
                DynamicTest.dynamicTest("변경할 송금ID가 중복된 경우 예외가 발생한다.", () -> {
                    //when //then
                    String updateName = "ex";
                    assertThatThrownBy(() -> userService.updateRemittanceId(user1.getCheckId(),updateName))
                            .isInstanceOf(DuplicateRemittanceIdException.class)
                            .hasMessage(updateName + "은 중복된 송금ID입니다.");
                })
        );
    }
}