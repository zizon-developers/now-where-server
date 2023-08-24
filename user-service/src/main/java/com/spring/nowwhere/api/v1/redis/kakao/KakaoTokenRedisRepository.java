package com.spring.nowwhere.api.v1.redis.kakao;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KakaoTokenRedisRepository extends CrudRepository<KakaoTokenFromRedis,String> {
    Optional<KakaoTokenFromRedis> findByEmail(String email);
}
