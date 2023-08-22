package com.spring.nowwhere.api.v1.redis.kakao;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash("kakaoAccessToken")
public class KakaoTokenFromRedis {
    @Id
    private String id;

    @Indexed
    private String email;

    @TimeToLive
    private Long expiration;

    public static KakaoTokenFromRedis createKakaoTokenFromRedis(String accessToken, String email,
                                                              Long remainingMilliSeconds){
        return KakaoTokenFromRedis.builder()
                .id(accessToken)
                .email(email)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }

    @Builder
    private KakaoTokenFromRedis(String id, String email, Long expiration) {
        this.id = id;
        this.email = email;
        this.expiration = expiration;
    }
}
