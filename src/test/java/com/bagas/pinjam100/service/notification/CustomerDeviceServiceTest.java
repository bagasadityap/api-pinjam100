package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.dto.notification.RegisterDeviceRequest;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.notification.CustomerDevice;
import com.bagas.pinjam100.repository.notification.CustomerDeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerDeviceServiceTest")
class CustomerDeviceServiceTest {

    private static final String FCM_TOKEN = "sample-fcm-token-12345";

    @Mock
    private CustomerDeviceRepository customerDeviceRepository;

    @InjectMocks
    private CustomerDeviceService customerDeviceService;

    private Customer customer;
    private RegisterDeviceRequest request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());

        request = new RegisterDeviceRequest(FCM_TOKEN);
    }

    @Nested
    @DisplayName("register")
    class RegisterTest {

        @Test
        @DisplayName("should create and save new device when fcmToken is not registered yet")
        void shouldRegisterNewDeviceWhenTokenNotFound() {
            when(customerDeviceRepository.findByFcmToken(FCM_TOKEN))
                    .thenReturn(Optional.empty());

            customerDeviceService.register(customer, request);

            ArgumentCaptor<CustomerDevice> deviceCaptor = ArgumentCaptor.forClass(CustomerDevice.class);
            verify(customerDeviceRepository).findByFcmToken(FCM_TOKEN);
            verify(customerDeviceRepository).save(deviceCaptor.capture());

            CustomerDevice savedDevice = deviceCaptor.getValue();
            assertNotNull(savedDevice);
            assertEquals(customer, savedDevice.getCustomer());
            assertEquals(FCM_TOKEN, savedDevice.getFcmToken());
            assertNotNull(savedDevice.getLastUsedAt());
            assertTrue(savedDevice.getLastUsedAt().isBefore(Instant.now().plusSeconds(1)));
        }

        @Test
        @DisplayName("should update existing device and lastUsedAt when fcmToken already exists")
        void shouldUpdateExistingDeviceWhenTokenFound() {
            UUID existingDeviceId = UUID.randomUUID();
            Customer previousCustomer = new Customer();
            previousCustomer.setId(UUID.randomUUID());

            Instant oldLastUsedAt = Instant.now().minusSeconds(3600);

            CustomerDevice existingDevice = new CustomerDevice();
            existingDevice.setId(existingDeviceId);
            existingDevice.setCustomer(previousCustomer);
            existingDevice.setFcmToken(FCM_TOKEN);
            existingDevice.setLastUsedAt(oldLastUsedAt);

            when(customerDeviceRepository.findByFcmToken(FCM_TOKEN))
                    .thenReturn(Optional.of(existingDevice));

            customerDeviceService.register(customer, request);

            ArgumentCaptor<CustomerDevice> deviceCaptor = ArgumentCaptor.forClass(CustomerDevice.class);
            verify(customerDeviceRepository).findByFcmToken(FCM_TOKEN);
            verify(customerDeviceRepository).save(deviceCaptor.capture());

            CustomerDevice updatedDevice = deviceCaptor.getValue();
            assertNotNull(updatedDevice);
            assertEquals(existingDeviceId, updatedDevice.getId());
            assertEquals(customer, updatedDevice.getCustomer());
            assertEquals(FCM_TOKEN, updatedDevice.getFcmToken());
            assertNotNull(updatedDevice.getLastUsedAt());

            assertTrue(updatedDevice.getLastUsedAt().isAfter(oldLastUsedAt));
        }
    }
}
