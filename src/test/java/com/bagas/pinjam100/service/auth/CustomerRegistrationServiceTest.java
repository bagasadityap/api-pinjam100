package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.PendingCustomerRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRegistrationServiceTest")
class CustomerRegistrationServiceTest {

    private static final String PHONE_NUMBER = "08123456789";
    private static final String EXPECTED_KEY = "customer:registration:" + PHONE_NUMBER;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CustomerRegistrationService customerRegistrationService;

    // =========================================================
    // SAVE
    // =========================================================

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should save pending registration to redis with 10 minutes TTL")
        void shouldSaveRegistrationToRedis() {
            PendingCustomerRegistration registration = createPendingRegistration();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            customerRegistrationService.save(registration);

            verify(redisTemplate).opsForValue();
            verify(valueOperations).set(
                    EXPECTED_KEY,
                    registration,
                    Duration.ofMinutes(10)
            );
        }
    }

    // =========================================================
    // GET
    // =========================================================

    @Nested
    @DisplayName("get")
    class GetTest {

        @Test
        @DisplayName("should return pending registration when found in redis")
        void shouldReturnPendingRegistrationWhenFound() {
            PendingCustomerRegistration expectedRegistration = createPendingRegistration();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(EXPECTED_KEY)).thenReturn(expectedRegistration);

            PendingCustomerRegistration result = customerRegistrationService.get(PHONE_NUMBER);

            assertNotNull(result);
            assertEquals(expectedRegistration.getPhoneNumber(), result.getPhoneNumber());
            assertEquals(expectedRegistration.getEmail(), result.getEmail());

            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(EXPECTED_KEY);
        }

        @Test
        @DisplayName("should return null when pending registration not found in redis")
        void shouldReturnNullWhenNotFound() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(EXPECTED_KEY)).thenReturn(null);

            PendingCustomerRegistration result = customerRegistrationService.get(PHONE_NUMBER);

            assertNull(result);

            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(EXPECTED_KEY);
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete registration key from redis")
        void shouldDeleteRegistrationFromRedis() {
            customerRegistrationService.delete(PHONE_NUMBER);

            verify(redisTemplate).delete(EXPECTED_KEY);
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private PendingCustomerRegistration createPendingRegistration() {
        return new PendingCustomerRegistration(
                "Bagas",
                "3171000000000001",
                "customer@example.com",
                PHONE_NUMBER,
                "encoded_password"
        );
    }
}
