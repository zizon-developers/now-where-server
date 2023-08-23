package com.spring.nowwhere.api.v1.auth;

import com.spring.nowwhere.api.v1.auth.exception.OauthKakaoApiException;
import com.spring.nowwhere.api.v1.dto.ResponseApi;
import com.spring.nowwhere.api.v1.entity.User;
import com.spring.nowwhere.api.v1.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.redis.kakao.KakaoTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.kakao.KakaoTokenRedisRepository;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenRedisRepository;
import com.spring.nowwhere.api.v1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@Tag(name = "OAuth", description = "카카오 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository; // 다음에 지우기
    private final KakaoTokenRedisRepository kakaoTokenRedisRepository;
    private final ResponseApi responseApi;
    

    @PostMapping("/join")
    @Operation(summary = "login", description = "카카오 계정을 통해서 회원가입할 수 있다.")
    public ResponseEntity<OAuthUserDto> registerWithKakaoAccount (@RequestBody OAuthCodeRequest OAuthCodeRequest) {

        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());

        OAuthUserDto user = userService.createUser(kakaoUser);
        return responseApi.success(user, "회원가입 성공", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "카카오 계정을 통해서 로그인할 수 있다.")
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
        return responseApi.success(user);
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
    @Operation(summary = "additionalUserConsent", description = "카카오 친구를 조회하기 위해서는 추가 동의가 필요하다.")
    public ResponseEntity<OAuthUserDto> additionalUserConsent(@RequestBody OAuthCodeRequest OAuthCodeRequest,
                                                              HttpServletResponse response){
        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());
        savedKakaoAccessToken(kakaoToken,kakaoUser.getEmail());

        //이메일 추가 동의로 인해서 변경될 수 있기 때문에 추가
        userService.updateEmail(kakaoUser);
        User user = userService.login(kakaoUser);

        String accessToken = tokenProvider.generateJwtAccessToken(user);
        String refreshToken = tokenProvider.generateJwtRefreshToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, refreshToken);

        return responseApi.success(kakaoUser);
    }

    @PostMapping("/logout")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "logout", description = "로그인을 성공한 사용자는 로그아웃을 할 수 있다.")
    public ResponseEntity<LogoutAccessTokenFromRedis> logout(HttpServletRequest request){

        String token = getTokenByReqeust(request);
        LogoutAccessTokenFromRedis logoutToken = userService.logout(token);

        return responseApi.success(logoutToken);
    }

    @GetMapping("/kakao/friends")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "find kakaoFriends", description = "사용자가 추가 동의를 한 경우 사용자의 카카오톡 친구를 조회할 수 있다.")
    public ResponseEntity<Map> getKakaoFriends(HttpServletRequest request) {
        String token = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromAccessToken(token);

        //다음에 예외처리 refresh token으로 재발급 or 다시 login처리
        KakaoTokenFromRedis kakaoTokenFromRedis = kakaoTokenRedisRepository.findByEmail(email)
                .orElseThrow(() -> new OauthKakaoApiException("kakao accessToken이 없습니다."));

        return responseApi.success(oAuthKakaoService.getKakaoFriends(kakaoTokenFromRedis.getId()));
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
