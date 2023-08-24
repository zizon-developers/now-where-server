package com.spring.nowwhere.api.v1.redis.refresh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
@Builder
public class RefreshTokenFromRedis {
    @Id
    private String id;

    @Indexed
    private String email;

    @TimeToLive
    private Long expiration;

    public static RefreshTokenFromRedis createRefreshToken(String refreshToken, String email,
                                                           Long remainingMilliSeconds){
        return RefreshTokenFromRedis.builder()
                .id(refreshToken)
                .email(email)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }
}
