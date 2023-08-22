package com.spring.userservice.v1.api.auth;

import com.spring.userservice.v1.api.auth.exception.OauthKakaoApiException;
import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.jwt.JwtProperties;
import com.spring.userservice.v1.api.jwt.TokenProvider;
import com.spring.userservice.v1.api.redis.kakao.KakaoTokenFromRedis;
import com.spring.userservice.v1.api.redis.kakao.KakaoTokenRedisRepository;
import com.spring.userservice.v1.api.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.userservice.v1.api.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.userservice.v1.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository; // 다음에 지우기
    private final KakaoTokenRedisRepository kakaoTokenRedisRepository;

    @PostMapping("/join")
    public ResponseEntity<OAuthUserDto> registerWithKakaoAccount (@RequestBody OAuthCodeRequest OAuthCodeRequest) {

        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());

        OAuthUserDto user = userService.createUser(kakaoUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<OAuthUserDto> loginWithKakaoAccount (@RequestBody OAuthCodeRequest OAuthCodeRequest,
                                                               HttpServletResponse response){

        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());
        savedKakaoAccessToken(kakaoToken,kakaoUser.getEmail());

        User user = userService.login(kakaoUser);

        String accessToken = tokenProvider.generateJwtAccessToken(user);
        String refreshToken = tokenProvider.generateJwtRefreshToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, refreshToken);
        return ResponseEntity.ok(OAuthUserDto.of(user));
    }
    private void savedKakaoAccessToken(TokenDto tokenDto, String email) {

        kakaoTokenRedisRepository.findByEmail(email).ifPresent(
                token -> {
                    kakaoTokenRedisRepository.delete(token);
                });
        kakaoTokenRedisRepository.save(KakaoTokenFromRedis
                .createKakaoTokenFromRedis(JwtProperties.TOKEN_PREFIX + tokenDto.getAccessToken(),
                                            email, createExpireTimeOfKakao()));
    }
    private static long createExpireTimeOfKakao(){
        //카카오 accessToken 만료시간 12시간
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 11);
        c.add(Calendar.MINUTE, 55);
        return c.getTime().getTime();
    }

    //추가 동의할 때 이메일 다르면 바꿔주어야 한다.
    @PostMapping("/consent")
    public ResponseEntity<OAuthUserDto> additionalUserConsent(@RequestBody OAuthCodeRequest OAuthCodeRequest,
                                                              HttpServletResponse response){
        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());
        savedKakaoAccessToken(kakaoToken,kakaoUser.getEmail());

        userService.updateEmail(kakaoUser);

        User user = userService.login(kakaoUser);

        String accessToken = tokenProvider.generateJwtAccessToken(user);
        String refreshToken = tokenProvider.generateJwtRefreshToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, refreshToken);

        return ResponseEntity.ok(kakaoUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutAccessTokenFromRedis> logout(HttpServletRequest request){

        String token = getTokenByReqeust(request);

        LogoutAccessTokenFromRedis logoutToken = userService.logout(token);
        return ResponseEntity.ok(logoutToken);
    }

    @GetMapping("/kakao/friends")
    public Map getKakaoFriends(HttpServletRequest request) {
        String token = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromAccessToken(token);

        //다음에 예외처리 refresh token으로 재발급 or 다시 login처리
        KakaoTokenFromRedis kakaoTokenFromRedis = kakaoTokenRedisRepository.findByEmail(email)
                .orElseThrow(() -> new OauthKakaoApiException("kakao accessToken이 없습니다."));

        return oAuthKakaoService.getKakaoFriends(kakaoTokenFromRedis.getId());
    }

    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.ACCESS_HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }

    @GetMapping("/callback/kakao")
    public ResponseEntity test(@RequestParam String code){
        log.info(code);
//        oAuthKakaoService.getKakaoAccessToken(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tokens/logout")
    public Iterable<LogoutAccessTokenFromRedis> findAllLogoutToken(){
        return logoutAccessTokenRedisRepository.findAll();
    }

    @GetMapping("/tokens/kakao")
    public Iterable<KakaoTokenFromRedis> findAllKakaoToken(){
        return kakaoTokenRedisRepository.findAll();
    }

    @DeleteMapping("/tokens/delete")
    public void kakaoDelete(){
        kakaoTokenRedisRepository.deleteAll();
    }

}
