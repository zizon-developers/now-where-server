package com.spring.userservice.v1.api.redis.logout;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Jpa 처럼 redis로 save, findBy 등을 사용할 수 있습니다. Repository를 이용해 구현했지만 RedisTemplate으로도 구현할 수 있습니다.
 */
public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessTokenFromRedis,String> {
    // @Indexed 사용한 필드만 가능
    Optional<LogoutAccessTokenFromRedis> findByEmail(String email);
}
