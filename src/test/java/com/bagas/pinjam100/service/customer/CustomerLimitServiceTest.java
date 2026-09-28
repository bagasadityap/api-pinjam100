package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerLimitServiceTest")
class CustomerLimitServiceTest {

    private static final UUID LIMIT_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final BigDecimal CREDIT_LIMIT = new BigDecimal("10000000");

    @Mock
    private CustomerLimitRepository customerLimitRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CustomerLimitService customerLimitService;

    @Nested
    @DisplayName("findAll")
    class FindAllTest {

        @Test
        @DisplayName("should return list of limit responses when active limits exist")
        void shouldReturnListOfLimitResponses() {
            CustomerLimit limit1 = createCustomerLimit(LIMIT_ID);
            CustomerLimit limit2 = createCustomerLimit(UUID.randomUUID());

            when(customerLimitRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(limit1, limit2));

            List<LimitResponse> result = customerLimitService.findAll();

            assertNotNull(result);
            assertEquals(2, result.size());

            verify(customerLimitRepository).findAllByDeletedDateIsNull();
        }

        @Test
        @DisplayName("should return empty list when no active limits exist")
        void shouldReturnEmptyListWhenNoLimitsExist() {
            when(customerLimitRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<LimitResponse> result = customerLimitService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(customerLimitRepository).findAllByDeletedDateIsNull();
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return limit response when limit found by id")
        void shouldReturnLimitResponseWhenFound() {
            CustomerLimit limit = createCustomerLimit(LIMIT_ID);

            when(customerLimitRepository.findByIdAndDeletedDateIsNull(LIMIT_ID))
                    .thenReturn(Optional.of(limit));

            LimitResponse result = customerLimitService.findByIdAndDeletedDateIsNull(LIMIT_ID);

            assertNotNull(result);

            verify(customerLimitRepository).findByIdAndDeletedDateIsNull(LIMIT_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when limit not found by id")
        void shouldThrowExceptionWhenLimitNotFound() {
            when(customerLimitRepository.findByIdAndDeletedDateIsNull(LIMIT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerLimitService.findByIdAndDeletedDateIsNull(LIMIT_ID)
            );

            assertEquals("Data limit tidak ditemukan", exception.getMessage());

            verify(customerLimitRepository).findByIdAndDeletedDateIsNull(LIMIT_ID);
        }
    }

    @Nested
    @DisplayName("findByCustomer_Id")
    class FindByCustomerIdTest {

        @Test
        @DisplayName("should return limit response when limit found by customer id")
        void shouldReturnLimitResponseWhenCustomerFound() {
            CustomerLimit limit = createCustomerLimit(LIMIT_ID);

            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(limit));

            LimitResponse result = customerLimitService.findByCustomer_Id(CUSTOMER_ID);

            assertNotNull(result);

            verify(customerLimitRepository).findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when limit not found by customer id")
        void shouldThrowExceptionWhenLimitNotFoundForCustomer() {
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerLimitService.findByCustomer_Id(CUSTOMER_ID)
            );

            assertEquals("Data limit tidak ditemukan", exception.getMessage());

            verify(customerLimitRepository).findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should save limit and send notification successfully")
        void shouldSaveLimitAndSendNotificationSuccessfully() {
            LimitRequest request = createLimitRequest();
            Customer customer = createCustomer();

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerLimitRepository.save(any(CustomerLimit.class)))
                    .thenAnswer(invocation -> {
                        CustomerLimit limit = invocation.getArgument(0);
                        limit.setId(LIMIT_ID);
                        return limit;
                    });

            LimitResponse result = customerLimitService.save(request);

            assertNotNull(result);

            verify(customerRepository).findByIdAndDeletedDateIsNull(CUSTOMER_ID);
            verify(customerLimitRepository).save(any(CustomerLimit.class));
            verify(notificationService).sendToCustomer(
                    eq(customer),
                    eq("Akun Anda Berhasil Diverifikasi"),
                    eq("Akun Anda telah berhasil diverifikasi. Anda kini dapat mulai mengajukan pinjaman."),
                    eq("verification"),
                    isNull()
            );
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when customer not found during save")
        void shouldThrowExceptionWhenCustomerNotFoundOnSave() {
            LimitRequest request = createLimitRequest();

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerLimitService.save(request)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());

            verify(customerRepository).findByIdAndDeletedDateIsNull(CUSTOMER_ID);
            verify(customerLimitRepository, never()).save(any());
            verifyNoInteractions(notificationService);
        }
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setFullName("Bagas Aditya");
        return customer;
    }

    private CustomerLimit createCustomerLimit(UUID id) {
        CustomerLimit limit = new CustomerLimit();
        limit.setId(id);
        limit.setCustomer(createCustomer());
        limit.setCreditLimit(CREDIT_LIMIT);
        limit.setAvailableLimit(CREDIT_LIMIT);
        return limit;
    }

    private LimitRequest createLimitRequest() {
        LimitRequest request = new LimitRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setCreditLimit(CREDIT_LIMIT);
        return request;
    }
}
