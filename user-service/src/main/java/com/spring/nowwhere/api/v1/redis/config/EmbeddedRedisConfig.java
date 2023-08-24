package com.spring.nowwhere.api.v1.redis.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * RedisConfig와 다른점은 Embedd Redis를 띄운다는 것이고 해당 포트가 미사용중이라면 사용하고 사용중이랑 그외 다른 포트를 사용하도록 하는 설정입니다.
 */
@Slf4j
@Configuration
@Profile("local")
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.host}")
    private String host;

    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
        // 패스워드가 있는경우
        // lettuceConnectionFactory.setPassword("");
        return lettuceConnectionFactory;
    }

    @PostConstruct
    public void redisServer() throws IOException {
        redisServer = new RedisServer(port);
        log.info("port ={}",port);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}
