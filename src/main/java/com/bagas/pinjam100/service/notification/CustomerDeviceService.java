package com.bagas.pinjam100.service.notification;

import com.bagas.pinjam100.dto.notification.RegisterDeviceRequest;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.notification.CustomerDevice;
import com.bagas.pinjam100.repository.notification.CustomerDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CustomerDeviceService {

    private final CustomerDeviceRepository customerDeviceRepository;

    public CustomerDeviceService(CustomerDeviceRepository customerDeviceRepository) {
        this.customerDeviceRepository = customerDeviceRepository;
    }

    @Transactional
    public void register(Customer customer, RegisterDeviceRequest request) {
        CustomerDevice device = customerDeviceRepository
                .findByFcmToken(request.fcmToken())
                .orElseGet(CustomerDevice::new);

        device.setCustomer(customer);
        device.setFcmToken(request.fcmToken());
        device.setLastUsedAt(Instant.now());

        customerDeviceRepository.save(device);
    }
}
