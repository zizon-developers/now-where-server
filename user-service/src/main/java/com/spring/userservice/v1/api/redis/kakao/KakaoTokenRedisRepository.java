package com.spring.userservice.v1.api.redis.kakao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Jpa 처럼 redis로 save, findBy 등을 사용할 수 있습니다. Repository를 이용해 구현했지만 RedisTemplate으로도 구현할 수 있습니다.
 */
public interface KakaoTokenRedisRepository extends CrudRepository<KakaoTokenFromRedis,String> {
    // @Indexed 사용한 필드만 가능
    Optional<KakaoTokenFromRedis> findByEmail(String email);
}
