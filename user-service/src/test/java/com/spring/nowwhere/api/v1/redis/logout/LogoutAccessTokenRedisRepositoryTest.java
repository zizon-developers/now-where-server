package com.spring.nowwhere.api.v1.redis.logout;

import com.spring.nowwhere.api.v1.redis.refresh.RefreshTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LogoutAccessTokenRedisRepositoryTest {

    @Autowired
    private LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @AfterEach
    void tearDown(){
        logoutAccessTokenRedisRepository.deleteAll();
        refreshTokenRedisRepository.deleteAll();
    }

    @DisplayName("email을 통해서 redis에 저장되어 있는 Logout된 accessToken을 얻을 수 있다.")
    @Test
    public void findByEmail(){
        //given
        String accessToken = "accessToken";
        String email = "email";
        Long expiration = 3000L;
        LogoutAccessTokenFromRedis logoutAccessToken = LogoutAccessTokenFromRedis.createLogoutAccessToken(accessToken, email, expiration);

        //when
        logoutAccessTokenRedisRepository.save(logoutAccessToken);

        //then
        LogoutAccessTokenFromRedis find = logoutAccessTokenRedisRepository.findByEmail(email).get();

        assertAll(
                () -> assertEquals(accessToken,find.getId()),
                () -> assertEquals(email,find.getEmail()),
                () -> assertEquals(expiration/1000,find.getExpiration())
        );
    }
}