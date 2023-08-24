package com.spring.nowwhere.api.v1.redis.refresh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshTokenFromRedisTest {

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @AfterEach
    void tearDown(){
        refreshTokenRedisRepository.deleteAll();
    }

    @DisplayName("email을 통해서 redis에 저장된 refresh token을 찾을 수 있다.")
    @Test
    public void findByEmail(){
        //given
        String refreshToken = "refreshToken";
        String email = "email";
        Long expiration = 3000L;
        RefreshTokenFromRedis refreshTokenFromRedis = RefreshTokenFromRedis
                                                        .createRefreshToken(refreshToken, email, expiration);
        //when
        refreshTokenRedisRepository.save(refreshTokenFromRedis);

        //then
        RefreshTokenFromRedis find = refreshTokenRedisRepository.findByEmail(email).get();

        assertAll(
                () -> assertEquals(refreshToken,find.getId()),
                () -> assertEquals(email,find.getEmail()),
                () -> assertEquals(expiration/1000,find.getExpiration())
        );
    }
}