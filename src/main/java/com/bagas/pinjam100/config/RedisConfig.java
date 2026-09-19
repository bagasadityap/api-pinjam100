package com.bagas.pinjam100.config;

import com.bagas.pinjam100.config.prop.AppConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class RedisConfig {

    private final AppConfigProperties appConfigProp;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(appConfigProp.getRedis().getHost());
        config.setPort(appConfigProp.getRedis().getPort());

        // Handling username (hanya set jika tidak kosong)
        if (StringUtils.hasText(appConfigProp.getRedis().getUsername())) {
            config.setUsername(appConfigProp.getRedis().getUsername());
        }

        // Handling password
        if (StringUtils.hasText(appConfigProp.getRedis().getPassword())) {
            config.setPassword(RedisPassword.of(appConfigProp.getRedis().getPassword()));
        }

        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(appConfigProp.getRedis().getLettucePoolMaxActive());
        poolConfig.setMaxIdle(appConfigProp.getRedis().getLettucePoolMaxIdle());
        poolConfig.setMinIdle(appConfigProp.getRedis().getLettucePoolMinIdle());
        poolConfig.setMaxWait(appConfigProp.getRedis().getLettucePoolMaxWait());

        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(appConfigProp.getRedis().getTimeout())
                .poolConfig(poolConfig)
                .build();

        return new LettuceConnectionFactory(config, clientConfiguration);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.bagas.pinjam100")
                .allowIfBaseType(Object.class)
                .build();

        objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );

        String prefix = normalizePrefix(appConfigProp.getRedis().getKeyPrefix());
        if (!prefix.isEmpty()) {
            config = config.computePrefixWith(CacheKeyPrefix.prefixed(prefix));
        }

        return config;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null) return "";
        String p = prefix.trim();
        if (p.isEmpty()) return "";
        return p.endsWith(":") ? p : (p + ":");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}