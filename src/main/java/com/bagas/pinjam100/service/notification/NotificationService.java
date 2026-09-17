package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.notification.CustomerDevice;
import com.bagas.pinjam100.exception.InvalidFcmTokenException;
import com.bagas.pinjam100.repository.notification.CustomerDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final FirebaseNotificationService firebaseNotificationService;
    private final CustomerDeviceRepository customerDeviceRepository;

    public NotificationService(
            FirebaseNotificationService firebaseNotificationService,
            CustomerDeviceRepository customerDeviceRepository
    ) {
        this.firebaseNotificationService = firebaseNotificationService;
        this.customerDeviceRepository = customerDeviceRepository;
    }

    public void sendToCustomer(
            Customer customer,
            String title,
            String body,
            String channel,
            String deeplink
    ) {
        List<CustomerDevice> devices =
                customerDeviceRepository.findAllByCustomer(customer);

        for (CustomerDevice device : devices) {
            try {
                firebaseNotificationService.sendToToken(
                        device.getFcmToken(),
                        title,
                        body,
                        channel,
                        deeplink
                );
            } catch (InvalidFcmTokenException e) {
                customerDeviceRepository.delete(device);
            } catch (RuntimeException e) {
                System.err.println(
                        "Gagal mengirim FCM notification ke device "
                                + device.getId()
                );
            }
        }
    }

    public void sendCommon(
            String title,
            String body,
            String channel,
            String deeplink
    ) {
        firebaseNotificationService.sendToTopic(
                NotificationTopic.COMMON,
                title,
                body,
                channel,
                deeplink
        );
    }

    public void sendPromo(
            String title,
            String body,
            String channel,
            String deeplink
    ) {
        firebaseNotificationService.sendToTopic(
                NotificationTopic.PROMO,
                title,
                body,
                channel,
                deeplink
        );
    }
}