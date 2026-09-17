package com.bagas.pinjam100.repository.notification;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.notification.CustomerDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerDeviceRepository extends JpaRepository<CustomerDevice, UUID> {
    Optional<CustomerDevice> findByFcmToken(String fcmToken);
    List<CustomerDevice> findAllByCustomer(Customer customer);
    void deleteAllByCustomerId(UUID customerId);
}