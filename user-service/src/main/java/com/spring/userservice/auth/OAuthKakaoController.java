package com.spring.userservice.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("oauth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;

    @GetMapping("/kakao")
    public void kakaoCallback(@RequestBody OAuthDto OAuthDto) {
      log.info("mycode={}", OAuthDto.getCode());
        oAuthKakaoService.getKakaoAccessToken(OAuthDto.getCode());
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
