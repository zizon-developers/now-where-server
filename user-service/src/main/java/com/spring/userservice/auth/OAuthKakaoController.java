package com.spring.userservice.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("oauth")
public class OAuthKakaoController {

    private final OAuthKakaoService oAuthKakaoService;

    @GetMapping("/kakao")
    public ResponseEntity kakaoCallback(@RequestBody String code) {
      log.info(code);
        oAuthKakaoService.getKakaoAccessToken(code);

        return ResponseEntity.ok().build();
    }

}
