package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.PendingCustomerRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private static final String PREFIX =
            "customer:registration:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(
            PendingCustomerRegistration registration
    ) {
        String key = PREFIX + registration.getPhoneNumber();

        redisTemplate.opsForValue().set(
                key,
                registration,
                Duration.ofMinutes(10)
        );
    }

    public PendingCustomerRegistration get(
            String phoneNumber
    ) {
        String key = PREFIX + phoneNumber;

        return (PendingCustomerRegistration)
                redisTemplate.opsForValue().get(key);
    }

    public void delete(
            String phoneNumber
    ) {
        String key = PREFIX + phoneNumber;

        redisTemplate.delete(key);
    }
}
