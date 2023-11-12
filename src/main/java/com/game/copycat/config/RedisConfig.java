package com.game.copycat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

//
//@Configuration
//@Slf4j
//@EnableRedisRepositories
//@EnableRedisHttpSession()
//@RequiredArgsConstructor
//public class RedisConfig {
//    private final ObjectMapper objectMapper;
//    @Value("${spring.data.redis.host}")
//    private String host;
//    @Value("${spring.data.redis.port}")
//    private int port;
//    @Value("${spring.data.redis.password}")
//    private String password;
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        log.info("redis host : {}", host);
//        log.info("redis port : {}", port);
//        log.info("redis password : {}", password);
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(host);
//        redisStandaloneConfiguration.setPort(port);
//        redisStandaloneConfiguration.setPassword(password);
//        return new LettuceConnectionFactory(
//                redisStandaloneConfiguration
//        );
//    }
//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        return new GenericJackson2JsonRedisSerializer();
//    }
//    @Bean
//    public StringRedisTemplate stringRedisTemplate() {
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//
//        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
//
//        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
//        stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
//        stringRedisTemplate.afterPropertiesSet();
//        return stringRedisTemplate;
//    }
////    @Bean
////    public RedisTemplate<Object, Object> sessionRedisTemplate(
////            RedisConnectionFactory connectionFactory) {
////        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
////        template.setKeySerializer(new StringRedisSerializer());
////        template.setHashKeySerializer(new StringRedisSerializer());
////
////        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
////
////        template.setConnectionFactory(connectionFactory);
////        return template;
////    }
//}
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60)
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {

    public RedisConfig(){
        super(RedisHttpSessionConfiguration.class);
    }

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        standaloneConfiguration.setPassword(password.isEmpty() ? RedisPassword.none() : RedisPassword.of(password));
        return new LettuceConnectionFactory(standaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}