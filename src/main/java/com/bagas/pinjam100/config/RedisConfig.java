package com.bagas.pinjam100.config;

import com.bagas.pinjam100.config.prop.AppConfigProperties;
import tools.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    private final AppConfigProperties appConfigProp;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(appConfigProp.getRedis().getHost());
        redisConfig.setPort(appConfigProp.getRedis().getPort());
        redisConfig.setUsername(appConfigProp.getRedis().getUsername());
        redisConfig.setPassword(RedisPassword.of(appConfigProp.getRedis().getPassword()));

        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(appConfigProp.getRedis().getLettucePoolMaxActive());
        poolConfig.setMaxIdle(appConfigProp.getRedis().getLettucePoolMaxIdle());
        poolConfig.setMinIdle(appConfigProp.getRedis().getLettucePoolMinIdle());
        poolConfig.setMaxWait(appConfigProp.getRedis().getLettucePoolMaxWait());

        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .clientResources(DefaultClientResources.create())
                .commandTimeout(appConfigProp.getRedis().getTimeout())
                .poolConfig(poolConfig)
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return baseConfiguration(Duration.ofHours(1));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheTtls() {
        return builder -> {
            builder.withCacheConfiguration(
                    CacheNames.CACHE_USER,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_USER_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_ROLE,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_ROLE_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_PERMISSION,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_PERMISSION_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_CUSTOMER,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_CUSTOMER_DETAIL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_CUSTOMER_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_LIMIT,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_LIMIT_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_DOCUMENT,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_DOCUMENT_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_DASHBOARD,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_INSTALLMENT,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_INSTALLMENT_APPLICATION,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_INSTALLMENT_CUSTOMER,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_LOAN,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_LOAN_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_TRANSACTION_HISTORY,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_BRANCH,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_BRANCH_ALL,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_PROVINCES,
                    baseConfiguration(Duration.ofHours(1))
            );
            builder.withCacheConfiguration(
                    CacheNames.CACHE_REGENCIES,
                    baseConfiguration(Duration.ofHours(1))
            );
        };
    }

    private RedisCacheConfiguration baseConfiguration(Duration ttl) {
        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                serializer
                        )
                );

        String prefix = normalizePrefix(appConfigProp.getRedis().getKeyPrefix());

        if (!prefix.isEmpty()) {
            config = config.computePrefixWith(CacheKeyPrefix.prefixed(prefix));
        }

        return config;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer valueSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null) {
            return "";
        }

        String normalized = prefix.trim();

        if (normalized.isEmpty()) {
            return "";
        }

        return normalized.endsWith(":") ? normalized : normalized + ":";
    }

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->
                params.length == 0
                        ? ""
                        : SimpleKeyGenerator.generateKey(params);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn(
                        "Cache read failed on {} (key {}); falling back to DB: {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn(
                        "Cache write failed on {} (key {}): {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn(
                        "Cache evict failed on {} (key {}): {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn(
                        "Cache clear failed on {}: {}",
                        cache.getName(),
                        exception.getMessage()
                );
            }
        };
    }
}