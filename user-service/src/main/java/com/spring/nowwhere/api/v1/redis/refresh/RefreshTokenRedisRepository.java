package com.spring.nowwhere.api.v1.redis.refresh;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenFromRedis,String> {

    Optional<RefreshTokenFromRedis> findByEmail(String email);
}
