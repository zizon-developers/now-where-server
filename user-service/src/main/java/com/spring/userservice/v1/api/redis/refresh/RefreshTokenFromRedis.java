package com.spring.userservice.v1.api.redis.refresh;

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
/**
 * RedisHash : Hash Collection 명시 -> Jpa의 Entity에 해당하는 애노테이션이라고 인지하면 됩니다.
 * value 값은 Key를 만들 때 사용하는 것으로 Hash의 Key는 value + @Id로 형성됩니다.
 * @Id : key를 식별할 떄 사용하는 고유한 값으로 @RedisHash와 결합해서 key를 생성하는 합니다.
 * 해당 애노테이션이 붙은 변수명은 반드시 id여야 합니다.
 * @Indexed : CRUD Repository를 사용할 때 jpa의 findBy필드명 처럼 사용하기 위해서 필요한 애노테이션입니다.
 * @TimeToLive : 유효시간 값으로 초단위 입니다. 유효 시간이 지나면 자동으로 삭제됩니다.
 * @TimeToLive(unit = TimeUnit.MILLISECONDS) 옵션으로 단위를 변경할 수 있습니다.
 */
public class RefreshTokenFromRedis {
    @Id
    private String id;

    @Indexed // 필드 값으로 데이터 찾을 수 있게 하는 어노테이션(findByAccessToken)
    private String accessToken;

    @Indexed // 필드 값으로 데이터 찾을 수 있게 하는 어노테이션(findByAccessToken)
    private String email;

    @TimeToLive
    private Long expiration; // seconds

    public static RefreshTokenFromRedis createRefreshToken(String refreshToken, String email, String accessToken,
                                                                Long remainingMilliSeconds){
        return RefreshTokenFromRedis.builder()
                .id(refreshToken)
                .accessToken(accessToken)
                .email(email)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }

    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
}
