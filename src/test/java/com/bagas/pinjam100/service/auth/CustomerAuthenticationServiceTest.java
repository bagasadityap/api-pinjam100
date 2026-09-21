package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerAuthenticationServiceTest")
class CustomerAuthenticationServiceTest {

    private static final String PHONE_NUMBER = "08123456789";
    private static final String UNKNOWN_PHONE_NUMBER = "08999999999";
    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerAuthenticationService customerAuthenticationService;

    // =========================================================
    // LOAD BY PHONE NUMBER
    // =========================================================

    @Nested
    @DisplayName("loadByPhoneNumber")
    class LoadByPhoneNumberTest {

        @Test
        @DisplayName("should return customer when found by phone number")
        void shouldReturnCustomerWhenFound() {
            Customer customer = createCustomer();

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));

            Customer result = customerAuthenticationService.loadByPhoneNumber(PHONE_NUMBER);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getId());
            assertEquals(PHONE_NUMBER, result.getPhoneNumber());

            verify(customerRepository).findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER);
        }

        @Test
        @DisplayName("should throw AuthenticationException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(UNKNOWN_PHONE_NUMBER))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerAuthenticationService.loadByPhoneNumber(UNKNOWN_PHONE_NUMBER)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());

            verify(customerRepository).findByPhoneNumberAndDeletedDateIsNull(UNKNOWN_PHONE_NUMBER);
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setPhoneNumber(PHONE_NUMBER);
        return customer;
    }
}
