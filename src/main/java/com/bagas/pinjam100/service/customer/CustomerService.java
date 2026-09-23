package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.config.CacheNames;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerDetailRepository customerDetailRepository;
    private final CustomerEmploymentRepository customerEmploymentRepository;
    private final CustomerLimitRepository customerLimitRepository;
    private final DocumentRepository documentRepository;
    private final RekeningRepository rekeningRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerDetailRepository customerDetailRepository,
            CustomerEmploymentRepository customerEmploymentRepository,
            CustomerLimitRepository customerLimitRepository,
            DocumentRepository documentRepository,
            RekeningRepository rekeningRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerDetailRepository = customerDetailRepository;
        this.customerEmploymentRepository = customerEmploymentRepository;
        this.customerLimitRepository = customerLimitRepository;
        this.documentRepository = documentRepository;
        this.rekeningRepository = rekeningRepository;
    }

    @Cacheable(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, key = "'all_active'")
    public List<CustomerResponse> findAllByDeletedDateIsNull() {
        return customerRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, key = "'pending'")
    public List<CustomerResponse> findPendingCustomer() {
        return customerRepository
                .findByVerificationStatusAndProfileCompletedAndDeletedDateIsNull(VerificationStatus.PENDING, true)
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, key = "'verified_no_limit'")
    public List<CustomerResponse> findVerifiedAndLimitIsNull() {
        return customerRepository
                .findVerifiedAndLimitIsNull(VerificationStatus.VERIFIED)
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#id")
    public CustomerResponse findByIdAndDeletedDateIsNull(UUID id) {
        return toCustomerResponse(getCustomer(id));
    }

    @Cacheable(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#customerId")
    public CustomerDetailResponse findDetailById(UUID customerId) {
        Customer customer = getCustomer(customerId);

        CustomerDetail detail = customerDetailRepository
                .findByCustomer_IdAndDeletedDateIsNull(customerId)
                .orElse(null);

        CustomerEmployment employment = customerEmploymentRepository
                .findByCustomer_IdAndDeletedDateIsNull(customerId)
                .orElse(null);

        CustomerLimit limit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(customerId)
                .orElse(null);

        List<Document> documents = documentRepository
                .findAllByCustomer_IdAndDeletedDateIsNull(customerId);

        List<Rekening> rekening = rekeningRepository
                .findAllByCustomer_IdAndDeletedDateIsNull(customerId);

        return new CustomerDetailResponse(
                customer,
                detail,
                employment,
                limit,
                documents,
                rekening
        );
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = getCustomer(id);

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());

        customerRepository.save(customer);

        return toCustomerResponse(customer);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public CustomerResponse delete(UUID id) {
        Customer customer = getCustomer(id);

        customer.setDeletedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return toCustomerResponse(customer);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public CustomerDetailResponse saveOnboarding(
            UUID customerId,
            CustomerOnboardingRequest request
    ) {
        Customer customer = getCustomer(customerId);

        createDetail(customer, request);
        createEmployment(customer, request);

        if (request.getRekenings() != null && !request.getRekenings().isEmpty()) {
            createRekenings(customer, request.getRekenings());
        }

        customer.setProfileCompleted(true);
        customerRepository.save(customer);

        return findDetailById(customerId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public CustomerDetailResponse updateOnboarding(
            UUID customerId,
            CustomerOnboardingRequest request
    ) {
        Customer customer = getCustomer(customerId);

        createDetail(customer, request);
        createEmployment(customer, request);

        if (request.getRekenings() != null && !request.getRekenings().isEmpty()) {
            createRekenings(customer, request.getRekenings());
        }

        customer.setProfileCompleted(true);
        customerRepository.save(customer);

        return findDetailById(customerId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public CustomerDetailResponse verifyCustomer(
            UUID customerId,
            VerificationStatus verificationStatus
    ) {
        Customer customer = getCustomer(customerId);

        customer.setVerificationStatus(verificationStatus);
        customerRepository.save(customer);

        return findDetailById(customerId);
    }

    private void createDetail(
            Customer customer,
            CustomerOnboardingRequest request
    ) {
        CustomerDetail detail = new CustomerDetail();

        detail.setCustomer(customer);
        detail.setBirthDate(LocalDate.parse(request.getBirthDate()));
        detail.setPlaceOfBirth(request.getPlaceOfBirth());
        detail.setGender(request.getGender());
        detail.setAddress(request.getAddress());
        detail.setProvince(request.getProvince());
        detail.setCity(request.getCity());
        detail.setDistrict(request.getDistrict());
        detail.setVillage(request.getVillage());
        detail.setPostalCode(request.getPostalCode());

        customerDetailRepository.save(detail);
    }

    private void updateDetail(
            Customer customer,
            CustomerDetailRequest request
    ) {
        CustomerDetail detail = customer.getDetail();

        if (detail == null) {
            detail = new CustomerDetail();
            detail.setCustomer(customer);
            customer.setDetail(detail);
        }

        detail.setBirthDate(request.getBirthDate());
        detail.setPlaceOfBirth(request.getPlaceOfBirth());
        detail.setGender(request.getGender());
        detail.setAddress(request.getAddress());
        detail.setProvince(request.getProvince());
        detail.setCity(request.getCity());
        detail.setDistrict(request.getDistrict());
        detail.setVillage(request.getVillage());
        detail.setPostalCode(request.getPostalCode());
    }

    private void createEmployment(
            Customer customer,
            CustomerOnboardingRequest request
    ) {
        CustomerEmployment employment = new CustomerEmployment();

        employment.setCustomer(customer);
        employment.setEmploymentType(request.getEmploymentType());
        employment.setCompanyName(request.getCompanyName());
        employment.setPosition(request.getPosition());
        employment.setMonthlyIncome(BigDecimal.valueOf(request.getMonthlyIncome()));
        employment.setStartDate(LocalDate.parse(request.getStartDate()));
        employment.setCompanyAddress(request.getCompanyAddress());
        employment.setCompanyPhone(request.getCompanyPhone());

        customerEmploymentRepository.save(employment);
    }

    private void updateEmployment(
            Customer customer,
            CustomerEmploymentRequest request
    ) {
        CustomerEmployment employment = customer.getEmployment();

        if (employment == null) {
            employment = new CustomerEmployment();
            employment.setCustomer(customer);
            customer.setEmployment(employment);
        }

        employment.setEmploymentType(request.getEmploymentType());
        employment.setCompanyName(request.getCompanyName());
        employment.setPosition(request.getPosition());
        employment.setMonthlyIncome(request.getMonthlyIncome());
        employment.setStartDate(request.getStartDate());
        employment.setCompanyAddress(request.getCompanyAddress());
        employment.setCompanyPhone(request.getCompanyPhone());
    }

    private void createRekenings(
            Customer customer,
            List<RekeningRequest> requests
    ) {
        customer.getRekenings().clear();

        requests.forEach(request -> {
            Rekening rekening = new Rekening();

            rekening.setCustomer(customer);
            rekening.setNamaBank(request.getNamaBank());
            rekening.setNoRekening(request.getNoRekening());
            rekening.setAccountHolder(request.getAccountHolder());

            customer.getRekenings().add(rekening);
        });
    }

    private void updateRekenings(
            Customer customer,
            List<RekeningRequest> requests
    ) {
        customer.getRekenings().clear();

        requests.forEach(request -> {
            Rekening rekening = new Rekening();

            rekening.setCustomer(customer);
            rekening.setNamaBank(request.getNamaBank());
            rekening.setNoRekening(request.getNoRekening());
            rekening.setAccountHolder(request.getAccountHolder());

            customer.getRekenings().add(rekening);
        });
    }

    private void updateRekening(
            Customer customer,
            UUID rekeningId,
            RekeningRequest request
    ) {
        Rekening rekening = rekeningRepository
                .findByIdAndCustomer_IdAndDeletedDateIsNull(
                        rekeningId,
                        customer.getId()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Rekening tidak ditemukan"
                        )
                );

        rekening.setNamaBank(request.getNamaBank());
        rekening.setNoRekening(request.getNoRekening());
        rekening.setAccountHolder(request.getAccountHolder());

        rekeningRepository.save(rekening);
    }

    private Customer getCustomer(UUID customerId) {
        return customerRepository
                .findByIdAndDeletedDateIsNull(customerId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Customer tidak ditemukan"
                        )
                );
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(customer.getId())
                .orElse(null);

        return new CustomerResponse(customer, customerLimit);
    }
}