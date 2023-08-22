package com.spring.userservice.v1.api.auth;

import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.jwt.JwtProperties;
import com.spring.userservice.v1.api.jwt.TokenProvider;
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

        User user = userService.login(kakaoUser);

        String accessToken = tokenProvider.generateJwtAccessToken(user);
        String refreshToken = tokenProvider.generateJwtRefreshToken(user);

        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, refreshToken);
        return ResponseEntity.ok(OAuthUserDto.of(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutAccessTokenFromRedis> logout(HttpServletRequest request){

        String token = request.getHeader(JwtProperties.ACCESS_HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        LogoutAccessTokenFromRedis logoutToken = userService.logout(token);
        return ResponseEntity.ok(logoutToken);
    }

    @GetMapping("/kakao/friends")
    public Map getKakaoFriends(HttpServletRequest request) {
        return oAuthKakaoService.getKakaoFriends(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @GetMapping("/callback/kakao")
    public ResponseEntity test(@RequestParam String code){
        log.info(code);
//        oAuthKakaoService.getKakaoAccessToken(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tokens/logout")
    public Iterable<LogoutAccessTokenFromRedis> findAllToken(){
        return logoutAccessTokenRedisRepository.findAll();
    }
}
