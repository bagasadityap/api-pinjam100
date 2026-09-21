package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.notification.CustomerDevice;
import com.bagas.pinjam100.exception.InvalidFcmTokenException;
import com.bagas.pinjam100.repository.notification.CustomerDeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceTest")
class NotificationServiceTest {

    private static final String TITLE = "Notifikasi Pinjaman";
    private static final String BODY = "Pengajuan Anda telah disetujui.";
    private static final String CHANNEL = "TRANSACTION";
    private static final String DEEPLINK = "pinjam100://transaction/123";

    @Mock
    private FirebaseNotificationService firebaseNotificationService;

    @Mock
    private CustomerDeviceRepository customerDeviceRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("sendToCustomer")
    class SendToCustomerTest {

        @Test
        @DisplayName("should send notification to all active devices of customer")
        void shouldSendNotificationToAllCustomerDevices() {
            CustomerDevice device1 = createCustomerDevice(UUID.randomUUID(), "token-1");
            CustomerDevice device2 = createCustomerDevice(UUID.randomUUID(), "token-2");

            when(customerDeviceRepository.findAllByCustomer(customer))
                    .thenReturn(List.of(device1, device2));

            notificationService.sendToCustomer(customer, TITLE, BODY, CHANNEL, DEEPLINK);

            verify(firebaseNotificationService).sendToToken("token-1", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(firebaseNotificationService).sendToToken("token-2", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(customerDeviceRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should delete device when InvalidFcmTokenException is thrown")
        void shouldDeleteDeviceWhenTokenIsInvalid() {
            CustomerDevice validDevice = createCustomerDevice(UUID.randomUUID(), "valid-token");
            CustomerDevice invalidDevice = createCustomerDevice(UUID.randomUUID(), "invalid-token");

            when(customerDeviceRepository.findAllByCustomer(customer))
                    .thenReturn(List.of(invalidDevice, validDevice));

            doThrow(new InvalidFcmTokenException("FCM token tidak terdaftar", new RuntimeException()))
                    .when(firebaseNotificationService)
                    .sendToToken("invalid-token", TITLE, BODY, CHANNEL, DEEPLINK);

            notificationService.sendToCustomer(customer, TITLE, BODY, CHANNEL, DEEPLINK);

            verify(firebaseNotificationService).sendToToken("invalid-token", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(firebaseNotificationService).sendToToken("valid-token", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(customerDeviceRepository).delete(invalidDevice);
            verify(customerDeviceRepository, never()).delete(validDevice);
        }

        @Test
        @DisplayName("should handle generic RuntimeException without stopping processing or deleting device")
        void shouldHandleGenericRuntimeExceptionAndContinue() {
            CustomerDevice errorDevice = createCustomerDevice(UUID.randomUUID(), "error-token");
            CustomerDevice validDevice = createCustomerDevice(UUID.randomUUID(), "valid-token");

            when(customerDeviceRepository.findAllByCustomer(customer))
                    .thenReturn(List.of(errorDevice, validDevice));

            doThrow(new RuntimeException("Network error"))
                    .when(firebaseNotificationService)
                    .sendToToken("error-token", TITLE, BODY, CHANNEL, DEEPLINK);

            notificationService.sendToCustomer(customer, TITLE, BODY, CHANNEL, DEEPLINK);

            verify(firebaseNotificationService).sendToToken("error-token", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(firebaseNotificationService).sendToToken("valid-token", TITLE, BODY, CHANNEL, DEEPLINK);
            verify(customerDeviceRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("sendCommon")
    class SendCommonTest {

        @Test
        @DisplayName("should send notification to COMMON topic successfully")
        void shouldSendCommonNotification() {
            notificationService.sendCommon(TITLE, BODY, CHANNEL, DEEPLINK);

            verify(firebaseNotificationService).sendToTopic(
                    NotificationTopic.COMMON,
                    TITLE,
                    BODY,
                    CHANNEL,
                    DEEPLINK
            );
        }
    }

    @Nested
    @DisplayName("sendPromo")
    class SendPromoTest {

        @Test
        @DisplayName("should send notification to PROMO topic successfully")
        void shouldSendPromoNotification() {
            notificationService.sendPromo(TITLE, BODY, CHANNEL, DEEPLINK);

            verify(firebaseNotificationService).sendToTopic(
                    NotificationTopic.PROMO,
                    TITLE,
                    BODY,
                    CHANNEL,
                    DEEPLINK
            );
        }
    }

    private CustomerDevice createCustomerDevice(UUID id, String fcmToken) {
        CustomerDevice device = new CustomerDevice();
        device.setId(id);
        device.setCustomer(customer);
        device.setFcmToken(fcmToken);
        return device;
    }
}
