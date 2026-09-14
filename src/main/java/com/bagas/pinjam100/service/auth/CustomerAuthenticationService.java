package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAuthenticationService {

    private final CustomerRepository customerRepository;

    public Customer loadByPhoneNumber(String phoneNumber) {
        return customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(phoneNumber)
                .orElseThrow(() ->
                        new AuthenticationException("Customer tidak ditemukan")
                );
    }
}