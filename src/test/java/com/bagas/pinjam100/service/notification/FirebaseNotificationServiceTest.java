package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.exception.InvalidFcmTokenException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FirebaseNotificationServiceTest")
class FirebaseNotificationServiceTest {

    private static final String FCM_TOKEN = "sample-fcm-token-12345";
    private static final String TITLE = "Notifikasi Pinjaman";
    private static final String BODY = "Pengajuan pinjaman Anda disetujui.";
    private static final String CHANNEL = "TRANSACTION";
    private static final String DEEPLINK = "pinjam100://transaction/detail/123";

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private FirebaseNotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new FirebaseNotificationService(firebaseMessaging);
    }

    @Nested
    @DisplayName("sendToToken")
    class SendToTokenTest {

        @Test
        @DisplayName("should send FCM notification to token successfully when token and parameters are valid")
        void shouldSendNotificationToTokenSuccessfully() throws Exception {
            when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/pinjam100/messages/msg123");

            assertDoesNotThrow(() ->
                    notificationService.sendToToken(FCM_TOKEN, TITLE, BODY, CHANNEL, DEEPLINK)
            );

            verify(firebaseMessaging).send(any(Message.class));
        }

        @Test
        @DisplayName("should handle null deeplink by converting it to empty string")
        void shouldHandleNullDeeplinkSuccessfully() throws Exception {
            when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/pinjam100/messages/msg123");

            assertDoesNotThrow(() ->
                    notificationService.sendToToken(FCM_TOKEN, TITLE, BODY, CHANNEL, null)
            );

            verify(firebaseMessaging).send(any(Message.class));
        }

        @Test
        @DisplayName("should throw InvalidFcmTokenException when token is UNREGISTERED")
        void shouldThrowInvalidFcmTokenExceptionWhenTokenUnregistered() throws Exception {
            FirebaseMessagingException fcmException = mock(FirebaseMessagingException.class);
            when(fcmException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);

            when(firebaseMessaging.send(any(Message.class))).thenThrow(fcmException);

            InvalidFcmTokenException exception = assertThrows(
                    InvalidFcmTokenException.class,
                    () -> notificationService.sendToToken(FCM_TOKEN, TITLE, BODY, CHANNEL, DEEPLINK)
            );

            assertEquals("FCM token sudah tidak terdaftar", exception.getMessage());
            verify(firebaseMessaging).send(any(Message.class));
        }

        @Test
        @DisplayName("should throw RuntimeException when FirebaseMessagingException occurs with other error codes")
        void shouldThrowRuntimeExceptionOnOtherFcmErrors() throws Exception {
            FirebaseMessagingException fcmException = mock(FirebaseMessagingException.class);
            when(fcmException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);

            when(firebaseMessaging.send(any(Message.class))).thenThrow(fcmException);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> notificationService.sendToToken(FCM_TOKEN, TITLE, BODY, CHANNEL, DEEPLINK)
            );

            assertEquals("Gagal mengirim FCM notification", exception.getMessage());
            verify(firebaseMessaging).send(any(Message.class));
        }
    }

    @Nested
    @DisplayName("sendToTopic")
    class SendToTopicTest {

        @Mock
        private NotificationTopic topic;

        @Test
        @DisplayName("should send FCM notification to topic successfully")
        void shouldSendNotificationToTopicSuccessfully() throws Exception {
            when(topic.value()).thenReturn("PROMO_TOPIC");
            when(firebaseMessaging.send(any(Message.class))).thenReturn("projects/pinjam100/messages/msg456");

            assertDoesNotThrow(() ->
                    notificationService.sendToTopic(topic, TITLE, BODY, CHANNEL, DEEPLINK)
            );

            verify(firebaseMessaging).send(any(Message.class));
        }

        @Test
        @DisplayName("should throw RuntimeException when sending to topic fails")
        void shouldThrowRuntimeExceptionWhenTopicSendingFails() throws Exception {
            when(topic.value()).thenReturn("PROMO_TOPIC");
            FirebaseMessagingException fcmException = mock(FirebaseMessagingException.class);

            when(firebaseMessaging.send(any(Message.class))).thenThrow(fcmException);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> notificationService.sendToTopic(topic, TITLE, BODY, CHANNEL, DEEPLINK)
            );

            assertEquals("Gagal mengirim FCM notification", exception.getMessage());
            verify(firebaseMessaging).send(any(Message.class));
        }
    }
}
