package com.spring.userservice.v1.api.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories // Redis Repository 활성화
public class RedisConfig {
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.host}")
    private String host;

    /**
     * redisConnectionFactory : RedisConnectionFactory를 통해 내장 혹은 외부의 Redis를 연결합니다.
     * 클라이언트로는 Jedis와 Lettuce가 있는데 spring 2.0부터는 JedisConnectionFactory가 deprecated 되었습니다
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
        // 패스워드가 있는경우
        // lettuceConnectionFactory.setPassword("");
        return lettuceConnectionFactory;
    }

    /**
     * redisTemplate : RedisTemplate을 통해 RedisConnection에서
     * 넘겨준 byte 값을 객체 직렬화합니다. Redis와 통신할 때 사용합니다.
     * 여기서는 <String, Object> 형식의 Template를 생성하였는데 필요한 형식이 있으면 추가하여 Bean으로 등록하면 됩니다!
     * 그리고 cache 기능, redisConnectionFactory 분리 등등 다양한 기능들을 추가할 수 있습니다.
     */
//    @Bean
//    public RedisTemplate<?, ?> redisTemplate() {
//        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
//    }
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        // redisTemplate를 받아와서 set, get, delete를 사용
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        /**
         * setKeySerializer, setValueSerializer 설정
         * redis-cli을 통해 직접 데이터를 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
         */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}
