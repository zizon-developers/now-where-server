package com.spring.nowwhere.api.v1.auth;

import com.spring.nowwhere.api.v1.auth.dto.KaKaoFriendDto;
import com.spring.nowwhere.api.v1.auth.dto.OAuthCodeRequest;
import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.auth.dto.TokenDto;
import com.spring.nowwhere.api.v1.auth.exception.OauthKakaoApiException;
import com.spring.nowwhere.api.v1.response.ResponseApi;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.redis.kakao.KakaoTokenFromRedis;
import com.spring.nowwhere.api.v1.redis.kakao.KakaoTokenRedisRepository;
import com.spring.nowwhere.api.v1.entity.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Tag(name = "OAuth", description = "카카오 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final KakaoTokenRedisRepository kakaoTokenRedisRepository;
    private final ResponseApi responseApi;
    
    @PostMapping("/login")
    @Operation(summary = "login", description = "카카오 계정을 통해서 로그인할 수 있으며 서버에 회원가입이 안되어 있으면 회원가입도 완료된다.")
    public ResponseEntity loginWithKakaoAccount (@RequestBody OAuthCodeRequest OAuthCodeRequest,
                                                               HttpServletResponse response){

        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());
        savedKakaoAccessToken(kakaoToken,kakaoUser.getEmail());

        OAuthUserDto oAuthUserDto = userService.checkAndRegisterUser(kakaoUser);
        TokenDto tokenDto = userService.login(oAuthUserDto);

        response.addHeader(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken());
        response.addCookie(createCookie(tokenDto.getRefreshToken()));
        return responseApi.success("로그인 성공");
    }

    @PostMapping("/grant-permission")
    @Operation(summary = "추가 동의 받기",
            description = "카카오 친구를 조회하기 위해서는 추가 동의가 필요하다. 이메일도 동의할 경우 userEmail 정보가 변경된다.")
    public ResponseEntity<OAuthUserDto> additionalUserConsent(@RequestBody OAuthCodeRequest OAuthCodeRequest,
                                                              HttpServletResponse response){
        log.info("mycode={}", OAuthCodeRequest.getCode());
        TokenDto kakaoToken = oAuthKakaoService.getKakaoToken(OAuthCodeRequest.getCode());
        OAuthUserDto kakaoUser = oAuthKakaoService.getKakaoUser(kakaoToken.getAccessToken());
        savedKakaoAccessToken(kakaoToken,kakaoUser.getEmail());

        //이메일 추가 동의로 인해서 변경될 수 있기 때문에 추가
        User user = userService.updateEmail(kakaoUser);

        String accessToken = tokenProvider.generateJwtAccessToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);
        return responseApi.success("추가 동의하기에 성공했습니다.");
    }

    private Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie(JwtProperties.REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        return cookie;
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



    @GetMapping("/friends")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "find kakao friends", description = "사용자가 추가 동의를 한 경우 사용자의 카카오톡 친구를 조회할 수 있다.")
    public ResponseEntity<Map> getKakaoFriends(@RequestBody(required = false) Optional<KaKaoFriendDto> kaKaoFriendDto,
                                               HttpServletRequest request) {

        String token = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromAccessToken(token);

        KakaoTokenFromRedis kakaoTokenFromRedis = kakaoTokenRedisRepository.findByEmail(email)
                .orElseThrow(() -> new OauthKakaoApiException("kakao accessToken이 없습니다."));
        String kakaoToken = kakaoTokenFromRedis.getId();

        KaKaoFriendDto setKaKaoFriendDto = kaKaoFriendDto.orElse(KaKaoFriendDto.builder()
                .offset(0)
                .limit(10)
                .order("asc")
                .friendOrder("favorite")
                .build());

        return responseApi.success(oAuthKakaoService.getKakaoFriends(kakaoToken, setKaKaoFriendDto));
    }
    private static String getTokenByReqeust(HttpServletRequest request) {
        return request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");
    }
    @GetMapping("/callback/kakao")
    public ResponseEntity test(@RequestParam String code){
        log.info(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "logout", description = "로그인을 성공한 사용자는 로그아웃을 할 수 있다.(refresh token도 삭제)")
    public ResponseEntity logout(HttpServletRequest request){
        String token = getTokenByReqeust(request);
        userService.logout(token);
        return responseApi.success("logout 되었습니다.");
    }

    @PostMapping("/access-token")
    @Operation(security = { @SecurityRequirement(name = "bearer-key (refresh token)") },
            summary = "reissue", description = "refresh token을 이용해서 access token을 재발행 가능하다. (user정보 넘겨줄 수 있는지 FE랑 이야기)")
    public ResponseEntity reissue(HttpServletRequest request,
                                  HttpServletResponse response){

        String refreshToken = getTokenByReqeust(request);
        String email = tokenProvider.getUserEmailFromRefreshToken(refreshToken);

        User user = userService.reissueWithUserVerification(email);
        String accessToken = tokenProvider.generateJwtAccessToken(user);
        response.addHeader(JwtProperties.ACCESS_TOKEN, accessToken);

        return responseApi.success("access token 재발급 되었습니다.");
    }


//    @GetMapping("/payment/kakao")
//    public Object requestKakaoPayPayment(HttpServletRequest request){
//        String token = getTokenByReqeust(request);
//        String email = tokenProvider.getUserEmailFromAccessToken(token);
//
//        KakaoTokenFromRedis kakaoTokenFromRedis = kakaoTokenRedisRepository.findByEmail(email)
//                .orElseThrow(() -> new OauthKakaoApiException("kakao accessToken이 없습니다."));
//
//        String kakaoToken = kakaoTokenFromRedis.getId();
//
//        return oAuthKakaoService.createKakaoPayPayment("Ej8w4tsnb", 1, kakaoToken);
//    }
}
