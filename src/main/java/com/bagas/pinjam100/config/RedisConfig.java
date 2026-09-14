package com.bagas.pinjam100.config;

import com.bagas.pinjam100.config.prop.AppConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.resource.DefaultClientResources;
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
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

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
        config.setUsername(appConfigProp.getRedis().getUsername());
        config.setPassword(RedisPassword.of(appConfigProp.getRedis().getPassword()));

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder();

        builder
                .clientResources(DefaultClientResources.create())
                .commandTimeout(appConfigProp.getRedis().getTimeout())
                .poolConfig(new GenericObjectPoolConfig<>() {{
                    setMaxTotal(appConfigProp.getRedis().getLettucePoolMaxActive());
                    setMaxIdle(appConfigProp.getRedis().getLettucePoolMaxIdle());
                    setMinIdle(appConfigProp.getRedis().getLettucePoolMinIdle());
                    setMaxWait(appConfigProp.getRedis().getLettucePoolMaxWait());
                }});

        LettuceClientConfiguration clientConfiguration = builder.build();

        return new LettuceConnectionFactory(config, clientConfiguration);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        BasicPolymorphicTypeValidator validator =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.binar_bc.mvc.test")
                        .build();

        objectMapper.activateDefaultTyping(
                validator,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        );

        String prefix =
                normalizePrefix(appConfigProp.getRedis().getKeyPrefix());

        if (!prefix.isEmpty()) {
            config = config.computePrefixWith(
                    CacheKeyPrefix.prefixed(prefix)
            );
        }

        return config;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null) return "";
        String p = prefix.trim();
        if (p.isEmpty()) return "";
        return p.endsWith(":") ? p : (p + ":");
    }
}
