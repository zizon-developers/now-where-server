package com.spring.nowwhere.api.v1.redis.kakao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KakaoTokenRedisRepositoryTest {

    @Autowired
    private KakaoTokenRedisRepository kakaoTokenRedisRepository;

    @AfterEach
    void tearDown(){
        kakaoTokenRedisRepository.deleteAll();
    }

    @DisplayName("email을 통해서 redis에 저장되어 있는 kakao의 accessToken을 얻을 수 있다.")
    @Test
    public void findByEmail(){
        //given
        String kakaoAccessToken = "kakaoAccessToken";
        String email = "email";
        Long expiration = 3000L;
        KakaoTokenFromRedis kakaoTokenFromRedis = KakaoTokenFromRedis
                .createKakaoTokenFromRedis(kakaoAccessToken, email, expiration);

        //when
        kakaoTokenRedisRepository.save(kakaoTokenFromRedis);

        //then
        KakaoTokenFromRedis find = kakaoTokenRedisRepository.findByEmail(email).get();

        assertAll(
                () -> assertEquals(kakaoAccessToken,find.getId()),
                () -> assertEquals(email,find.getEmail()),
                () -> assertEquals(expiration/1000,find.getExpiration())
        );
    }
}