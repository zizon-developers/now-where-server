package com.spring.userservice.v1.api.auth;

import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.entity.UserRepository;
import com.spring.userservice.v1.api.jwt.JwtProperties;
import com.spring.userservice.v1.api.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/kakao")
    public ResponseEntity<OAuthUserDto> kakaoCallback(@RequestBody OAuthRequest OAuthRequest,
                                                      HttpServletResponse response) {
        log.info("mycode={}", OAuthRequest.getCode());
        OAuthUserDto user = oAuthKakaoService.getKakaoAccessToken(OAuthRequest.getCode());

        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("not saved user"));


        String accessToken = tokenProvider.generateJwtAccessToken(findUser);
        String refreshToken = tokenProvider.generateJwtRefreshToken(findUser);

        log.info("access token ={}", accessToken);
        log.info("refresh token ={}", refreshToken);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
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

}
