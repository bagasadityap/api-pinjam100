package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.dto.request.customer.CustomerDetailRequest;
import com.bagas.pinjam100.dto.request.customer.CustomerEmploymentRequest;
import com.bagas.pinjam100.dto.request.customer.CustomerOnboardingRequest;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.dto.request.customer.RekeningRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerDetail;
import com.bagas.pinjam100.entity.customer.CustomerEmployment;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.Document;
import com.bagas.pinjam100.entity.customer.Rekening;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.repository.customer.CustomerDetailRepository;
import com.bagas.pinjam100.repository.customer.CustomerEmploymentRepository;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.customer.DocumentRepository;
import com.bagas.pinjam100.repository.customer.RekeningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceTest")
class CustomerServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final String FULL_NAME = "Bagas Aditya";
    private static final String EMAIL = "customer@example.com";
    private static final String PHONE_NUMBER = "08123456789";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerDetailRepository customerDetailRepository;

    @Mock
    private CustomerEmploymentRepository customerEmploymentRepository;

    @Mock
    private CustomerLimitRepository customerLimitRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RekeningRepository rekeningRepository;

    @InjectMocks
    private CustomerService customerService;

    @Nested
    @DisplayName("findAllByDeletedDateIsNull")
    class FindAllByDeletedDateIsNullTest {

        @Test
        @DisplayName("should return customer responses when customers exist")
        void shouldReturnCustomerResponses() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);

            when(customerRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(customer));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            List<CustomerResponse> result = customerService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerRepository).findAllByDeletedDateIsNull();
            verify(customerLimitRepository).findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should return empty list when no customers exist")
        void shouldReturnEmptyList() {
            when(customerRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<CustomerResponse> result = customerService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(customerRepository).findAllByDeletedDateIsNull();
            verifyNoInteractions(customerLimitRepository);
        }
    }

    @Nested
    @DisplayName("findPendingCustomer")
    class FindPendingCustomerTest {

        @Test
        @DisplayName("should return pending customers")
        void shouldReturnPendingCustomers() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            customer.setVerificationStatus(VerificationStatus.PENDING);

            when(customerRepository.findByVerificationStatus(VerificationStatus.PENDING))
                    .thenReturn(List.of(customer));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            List<CustomerResponse> result = customerService.findPendingCustomer();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerRepository).findByVerificationStatus(VerificationStatus.PENDING);
        }

        @Test
        @DisplayName("should return empty list when no pending customers exist")
        void shouldReturnEmptyList() {
            when(customerRepository.findByVerificationStatus(VerificationStatus.PENDING))
                    .thenReturn(List.of());

            List<CustomerResponse> result = customerService.findPendingCustomer();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(customerRepository).findByVerificationStatus(VerificationStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("findVerifiedAndLimitIsNull")
    class FindVerifiedAndLimitIsNullTest {

        @Test
        @DisplayName("should return verified customers without limit")
        void shouldReturnVerifiedCustomersWithoutLimit() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            customer.setVerificationStatus(VerificationStatus.VERIFIED);

            when(customerRepository.findVerifiedAndLimitIsNull(VerificationStatus.VERIFIED))
                    .thenReturn(List.of(customer));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            List<CustomerResponse> result = customerService.findVerifiedAndLimitIsNull();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(customerRepository).findVerifiedAndLimitIsNull(VerificationStatus.VERIFIED);
        }

        @Test
        @DisplayName("should return empty list when no verified customers exist")
        void shouldReturnEmptyList() {
            when(customerRepository.findVerifiedAndLimitIsNull(VerificationStatus.VERIFIED))
                    .thenReturn(List.of());

            List<CustomerResponse> result = customerService.findVerifiedAndLimitIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(customerRepository).findVerifiedAndLimitIsNull(VerificationStatus.VERIFIED);
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return customer response when found")
        void shouldReturnCustomerResponse() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            CustomerResponse result = customerService.findByIdAndDeletedDateIsNull(CUSTOMER_ID);

            assertNotNull(result);
            verify(customerRepository).findByIdAndDeletedDateIsNull(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.findByIdAndDeletedDateIsNull(CUSTOMER_ID)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("findDetailById")
    class FindDetailByIdTest {

        @Test
        @DisplayName("should return detail when all relations exist")
        void shouldReturnDetailWithAllRelations() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerDetail detail = new CustomerDetail();
            CustomerEmployment employment = new CustomerEmployment();
            CustomerLimit limit = new CustomerLimit();
            Document document = new Document();
            Rekening rekening = new Rekening();

            detail.setCustomer(customer);
            employment.setCustomer(customer);
            limit.setCustomer(customer);
            document.setCustomer(customer);
            rekening.setCustomer(customer);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(detail));
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(employment));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(limit));
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of(document));
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of(rekening));

            CustomerDetailResponse result = customerService.findDetailById(CUSTOMER_ID);

            assertNotNull(result);
        }

        @Test
        @DisplayName("should return detail with null optional relations and empty collections")
        void shouldReturnDetailWithNullRelations() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.findDetailById(CUSTOMER_ID);

            assertNotNull(result);
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.findDetailById(CUSTOMER_ID)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update customer successfully")
        void shouldUpdateCustomerSuccessfully() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerRequest request = createCustomerRequest();

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            CustomerResponse result = customerService.update(CUSTOMER_ID, request);

            assertNotNull(result);
            assertEquals("Updated Name", customer.getFullName());
            assertEquals("updated@example.com", customer.getEmail());
            assertEquals("08999999999", customer.getPhoneNumber());
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.update(CUSTOMER_ID, createCustomerRequest())
            );

            verify(customerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should soft delete customer successfully")
        void shouldSoftDeleteCustomerSuccessfully() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            CustomerResponse result = customerService.delete(CUSTOMER_ID);

            assertNotNull(result);
            assertNotNull(customer.getDeletedDate());
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.delete(CUSTOMER_ID)
            );

            verify(customerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("saveOnboarding")
    class SaveOnboardingTest {

        @Test
        @DisplayName("should save onboarding with rekenings")
        void shouldSaveOnboardingWithRekenings() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerOnboardingRequest request = createOnboardingRequest(true);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.save(any(CustomerDetail.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerEmploymentRepository.save(any(CustomerEmployment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.saveOnboarding(CUSTOMER_ID, request);

            assertNotNull(result);
            assertTrue(customer.isProfileCompleted());
            assertEquals(1, customer.getRekenings().size());
            verify(customerDetailRepository).save(any(CustomerDetail.class));
            verify(customerEmploymentRepository).save(any(CustomerEmployment.class));
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("should save onboarding without rekenings when list is empty")
        void shouldSaveOnboardingWithoutRekeningsWhenEmpty() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerOnboardingRequest request = createOnboardingRequest(false);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.save(any(CustomerDetail.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerEmploymentRepository.save(any(CustomerEmployment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.saveOnboarding(CUSTOMER_ID, request);

            assertNotNull(result);
            assertTrue(customer.isProfileCompleted());
            assertTrue(customer.getRekenings().isEmpty());
        }

        @Test
        @DisplayName("should save onboarding without rekenings when list is null")
        void shouldSaveOnboardingWithoutRekeningsWhenNull() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerOnboardingRequest request = createOnboardingRequest(false);
            request.setRekenings(null);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.save(any(CustomerDetail.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerEmploymentRepository.save(any(CustomerEmployment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.saveOnboarding(CUSTOMER_ID, request);

            assertNotNull(result);
            assertTrue(customer.isProfileCompleted());
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.saveOnboarding(CUSTOMER_ID, createOnboardingRequest(true))
            );
        }
    }

    @Nested
    @DisplayName("updateOnboarding")
    class UpdateOnboardingTest {

        @Test
        @DisplayName("should update onboarding successfully")
        void shouldUpdateOnboardingSuccessfully() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerOnboardingRequest request = createOnboardingRequest(true);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.save(any(CustomerDetail.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerEmploymentRepository.save(any(CustomerEmployment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.updateOnboarding(CUSTOMER_ID, request);

            assertNotNull(result);
            assertTrue(customer.isProfileCompleted());
            assertEquals(1, customer.getRekenings().size());
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.updateOnboarding(CUSTOMER_ID, createOnboardingRequest(true))
            );
        }
    }

    @Nested
    @DisplayName("verifyCustomer")
    class VerifyCustomerTest {

        @Test
        @DisplayName("should update verification status successfully")
        void shouldVerifyCustomerSuccessfully() {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(customerDetailRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerEmploymentRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());
            when(documentRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(rekeningRepository.findAllByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(List.of());

            CustomerDetailResponse result = customerService.verifyCustomer(
                    CUSTOMER_ID,
                    VerificationStatus.VERIFIED
            );

            assertNotNull(result);
            assertEquals(VerificationStatus.VERIFIED, customer.getVerificationStatus());
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("should throw exception when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.verifyCustomer(CUSTOMER_ID, VerificationStatus.VERIFIED)
            );
        }
    }

    @Nested
    @DisplayName("Private Method Tests")
    class PrivateMethodTest {

        @Test
        @DisplayName("should create detail when detail does not exist")
        void shouldCreateDetailWhenNull() throws Exception {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerDetailRequest request = createCustomerDetailRequest();

            invokePrivate("updateDetail", Customer.class, CustomerDetailRequest.class, customer, request);

            assertNotNull(customer.getDetail());
            assertEquals(customer, customer.getDetail().getCustomer());
        }

        @Test
        @DisplayName("should update existing detail")
        void shouldUpdateExistingDetail() throws Exception {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerDetail detail = new CustomerDetail();
            detail.setCustomer(customer);
            customer.setDetail(detail);

            CustomerDetailRequest request = createCustomerDetailRequest();

            invokePrivate("updateDetail", Customer.class, CustomerDetailRequest.class, customer, request);

            assertEquals(LocalDate.of(1995, 1, 1), detail.getBirthDate());
            assertEquals("Jakarta", detail.getPlaceOfBirth());
            assertEquals(customer, detail.getCustomer());
        }

        @Test
        @DisplayName("should create employment when employment does not exist")
        void shouldCreateEmploymentWhenNull() throws Exception {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerEmploymentRequest request = createCustomerEmploymentRequest();

            invokePrivate("updateEmployment", Customer.class, CustomerEmploymentRequest.class, customer, request);

            assertNotNull(customer.getEmployment());
            assertEquals(customer, customer.getEmployment().getCustomer());
        }

        @Test
        @DisplayName("should update existing employment")
        void shouldUpdateExistingEmployment() throws Exception {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            CustomerEmployment employment = new CustomerEmployment();
            employment.setCustomer(customer);
            customer.setEmployment(employment);

            CustomerEmploymentRequest request = createCustomerEmploymentRequest();

            invokePrivate("updateEmployment", Customer.class, CustomerEmploymentRequest.class, customer, request);

            assertEquals("FULL_TIME", employment.getEmploymentType());
            assertEquals("PT Maju Mundur", employment.getCompanyName());
            assertEquals(customer, employment.getCustomer());
        }

        @Test
        @DisplayName("should update rekenings")
        void shouldUpdateRekenings() throws Exception {
            Customer customer = createCustomer(CUSTOMER_ID, FULL_NAME);
            customer.getRekenings().add(new Rekening());

            List<RekeningRequest> requests = List.of(createRekeningRequest());

            invokePrivate("updateRekenings", Customer.class, List.class, customer, requests);

            assertEquals(1, customer.getRekenings().size());
            assertEquals("BCA", customer.getRekenings().get(0).getNamaBank());
            assertEquals("1234567890", customer.getRekenings().get(0).getNoRekening());
            assertEquals("Bagas Aditya", customer.getRekenings().get(0).getAccountHolder());
        }

        @Test
        @DisplayName("should update rekening when rekening exists")
        void shouldUpdateRekening() throws Exception {
            Rekening rekening = new Rekening();
            rekening.setId(UUID.randomUUID());

            when(rekeningRepository.findByIdAndCustomer_IdAndDeletedDateIsNull(
                    rekening.getId(),
                    CUSTOMER_ID
            )).thenReturn(Optional.of(rekening));

            invokePrivate(
                    "updateRekening",
                    Customer.class,
                    UUID.class,
                    RekeningRequest.class,
                    createCustomer(CUSTOMER_ID, FULL_NAME),
                    rekening.getId(),
                    createRekeningRequest()
            );

            assertEquals("BCA", rekening.getNamaBank());
            assertEquals("1234567890", rekening.getNoRekening());
            assertEquals("Bagas Aditya", rekening.getAccountHolder());
            verify(rekeningRepository).save(rekening);
        }

        @Test
        @DisplayName("should throw exception when rekening does not exist")
        void shouldThrowExceptionWhenRekeningNotFound() throws Exception {
            UUID rekeningId = UUID.randomUUID();

            when(rekeningRepository.findByIdAndCustomer_IdAndDeletedDateIsNull(
                    rekeningId,
                    CUSTOMER_ID
            )).thenReturn(Optional.empty());

            InvocationTargetException exception = assertThrows(
                    InvocationTargetException.class,
                    () -> invokePrivate(
                            "updateRekening",
                            Customer.class,
                            UUID.class,
                            RekeningRequest.class,
                            createCustomer(CUSTOMER_ID, FULL_NAME),
                            rekeningId,
                            createRekeningRequest()
                    )
            );

            assertInstanceOf(
                    EntityNotFoundException.class,
                    exception.getCause()
            );
            assertEquals(
                    "Rekening tidak ditemukan",
                    exception.getCause().getMessage()
            );
        }
    }

    private Customer createCustomer(UUID id, String fullName) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFullName(fullName);
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);
        customer.setRekenings(new ArrayList<>());
        return customer;
    }

    private CustomerRequest createCustomerRequest() {
        CustomerRequest request = new CustomerRequest();
        request.setFullName("Updated Name");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("08999999999");
        return request;
    }

    private CustomerDetailRequest createCustomerDetailRequest() {
        CustomerDetailRequest request = new CustomerDetailRequest();
        request.setBirthDate(LocalDate.of(1995, 1, 1));
        request.setPlaceOfBirth("Jakarta");
        request.setGender("MALE");
        request.setAddress("Jl. Merdeka No. 1");
        request.setProvince("DKI Jakarta");
        request.setCity("Jakarta Selatan");
        request.setDistrict("Kebayoran Baru");
        request.setVillage("Senayan");
        request.setPostalCode("12190");
        return request;
    }

    private CustomerEmploymentRequest createCustomerEmploymentRequest() {
        CustomerEmploymentRequest request = new CustomerEmploymentRequest();
        request.setEmploymentType("FULL_TIME");
        request.setCompanyName("PT Maju Mundur");
        request.setPosition("Software Engineer");
        request.setMonthlyIncome(BigDecimal.valueOf(15000000L));
        request.setStartDate(LocalDate.of(2020, 1, 1));
        request.setCompanyAddress("Jl. Sudirman No. 10");
        request.setCompanyPhone("0215551234");
        return request;
    }

    private CustomerOnboardingRequest createOnboardingRequest(boolean includeRekenings) {
        CustomerOnboardingRequest request = new CustomerOnboardingRequest();
        request.setBirthDate("1995-01-01");
        request.setPlaceOfBirth("Jakarta");
        request.setGender("MALE");
        request.setAddress("Jl. Merdeka No. 1");
        request.setProvince("DKI Jakarta");
        request.setCity("Jakarta Selatan");
        request.setDistrict("Kebayoran Baru");
        request.setVillage("Senayan");
        request.setPostalCode("12190");
        request.setEmploymentType("FULL_TIME");
        request.setCompanyName("PT Maju Mundur");
        request.setPosition("Software Engineer");
        request.setMonthlyIncome(15000000L);
        request.setStartDate("2020-01-01");
        request.setCompanyAddress("Jl. Sudirman No. 10");
        request.setCompanyPhone("0215551234");

        if (includeRekenings) {
            request.setRekenings(List.of(createRekeningRequest()));
        }

        return request;
    }

    private RekeningRequest createRekeningRequest() {
        RekeningRequest request = new RekeningRequest();
        request.setNamaBank("BCA");
        request.setNoRekening("1234567890");
        request.setAccountHolder("Bagas Aditya");
        return request;
    }

    private <T> Object invokePrivate(
            String methodName,
            Class<?> parameterType1,
            Class<?> parameterType2,
            Object arg1,
            Object arg2
    ) throws Exception {
        Method method = CustomerService.class.getDeclaredMethod(
                methodName,
                parameterType1,
                parameterType2
        );
        method.setAccessible(true);
        return method.invoke(customerService, arg1, arg2);
    }

    private <T> Object invokePrivate(
            String methodName,
            Class<?> parameterType1,
            Class<?> parameterType2,
            Class<?> parameterType3,
            Object arg1,
            Object arg2,
            Object arg3
    ) throws Exception {
        Method method = CustomerService.class.getDeclaredMethod(
                methodName,
                parameterType1,
                parameterType2,
                parameterType3
        );
        method.setAccessible(true);
        return method.invoke(customerService, arg1, arg2, arg3);
    }
}