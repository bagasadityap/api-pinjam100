package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.exception.InvalidFcmTokenException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Profile("!test")
public class FirebaseNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseNotificationService() {
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    public void sendToToken(
            String token,
            String title,
            String body,
            String channel,
            String deeplink
    ) {
        Message message = Message.builder()
                .setToken(token)
                .putAllData(Map.of(
                        "title", title,
                        "body", body,
                        "channel", channel,
                        "deeplink", deeplink == null ? "" : deeplink
                ))
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                throw new InvalidFcmTokenException(
                        "FCM token sudah tidak terdaftar",
                        e
                );
            }

            throw new RuntimeException(
                    "Gagal mengirim FCM notification",
                    e
            );
        }
    }

    public void sendToTopic(
            NotificationTopic topic,
            String title,
            String body,
            String channel,
            String deeplink
    ) {
        Message message = Message.builder()
                .setTopic(topic.value())
                .putAllData(Map.of(
                        "title", title,
                        "body", body,
                        "channel", channel,
                        "deeplink", deeplink == null ? "" : deeplink
                ))
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(
                    "Gagal mengirim FCM notification",
                    e
            );
        }
    }
}