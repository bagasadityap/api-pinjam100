package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.entity.auth.CustomerRefreshToken;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.service.jwt.JwtService;
import com.bagas.pinjam100.service.jwt.TokenBlacklistService;
import com.bagas.pinjam100.service.notification.CustomerDeviceService;
import com.bagas.pinjam100.service.otp.OtpVerificationService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerAuthServiceTest")
class CustomerAuthServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final String PHONE_NUMBER = "08123456789";
    private static final String UNKNOWN_PHONE = "08999999999";
    private static final String EMAIL = "customer@example.com";
    private static final String NATIONAL_ID = "3171000000000001";
    private static final String RAW_PASSWORD = "Password123!";
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final String TOKEN = "mocked-jwt-token";
    private static final String REFRESH_TOKEN = "mocked-refresh-token";
    private static final String OTP_CODE = "123456";
    private static final String FCM_TOKEN = "mocked-fcm-token";
    private static final Instant EXPIRES_AT = Instant.now().plusSeconds(3600);

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private OtpVerificationService otpVerificationService;

    @Mock
    private CustomerDeviceService customerDeviceService;

    @Mock
    private CustomerRegistrationService customerRegistrationService;

    @Mock
    private CustomerRefreshTokenService customerRefreshTokenService;

    @InjectMocks
    private CustomerAuthService customerAuthService;

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("should login successfully without fcmToken")
        void shouldLoginSuccessfullyWithoutFcmToken() {
            CustomerLoginRequest request = createLoginRequest(null);
            Customer customer = createCustomer();

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getPassword(), customer.getPassword()))
                    .thenReturn(true);
            when(jwtService.issueCustomer(eq(customer), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(jwtService.getExpiration(TOKEN))
                    .thenReturn(EXPIRES_AT);
            when(customerRefreshTokenService.create(customer))
                    .thenReturn(REFRESH_TOKEN);

            ResponseEntity<BaseResponse<CustomerAuthResponse>> response =
                    customerAuthService.login(request, "Login Berhasil");

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Login Berhasil", response.getBody().getMessage());

            CustomerAuthResponse authData = response.getBody().getData();
            assertEquals(TOKEN, authData.getToken());
            assertEquals(REFRESH_TOKEN, authData.getRefreshToken());

            verify(customerDeviceService, never()).register(any(), any());
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("should login successfully and skip device registration when fcmToken is blank")
        void shouldLoginSuccessfullyWithBlankFcmToken() {
            CustomerLoginRequest request = createLoginRequest("   ");
            Customer customer = createCustomer();

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getPassword(), customer.getPassword()))
                    .thenReturn(true);
            when(jwtService.issueCustomer(eq(customer), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(jwtService.getExpiration(TOKEN))
                    .thenReturn(EXPIRES_AT);
            when(customerRefreshTokenService.create(customer))
                    .thenReturn(REFRESH_TOKEN);

            ResponseEntity<BaseResponse<CustomerAuthResponse>> response =
                    customerAuthService.login(request, "Login Berhasil");

            assertNotNull(response);
            verify(customerDeviceService, never()).register(any(), any());
        }

        @Test
        @DisplayName("should login successfully and register device when fcmToken provided")
        void shouldLoginSuccessfullyWithFcmToken() {
            CustomerLoginRequest request = createLoginRequest(FCM_TOKEN);
            Customer customer = createCustomer();

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getPassword(), customer.getPassword()))
                    .thenReturn(true);
            when(jwtService.issueCustomer(eq(customer), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(jwtService.getExpiration(TOKEN))
                    .thenReturn(EXPIRES_AT);
            when(customerRefreshTokenService.create(customer))
                    .thenReturn(REFRESH_TOKEN);

            ResponseEntity<BaseResponse<CustomerAuthResponse>> response =
                    customerAuthService.login(request, "Login Berhasil");

            assertNotNull(response);
            verify(customerDeviceService).register(eq(customer), any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when phone number not found")
        void shouldThrowExceptionWhenPhoneNotFound() {
            CustomerLoginRequest request = createLoginRequest(null);

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerAuthService.login(request, "Login Berhasil")
            );

            assertEquals("Nomor telepon atau password salah", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("should throw AuthenticationException when password does not match")
        void shouldThrowExceptionWhenPasswordInvalid() {
            CustomerLoginRequest request = createLoginRequest(null);
            Customer customer = createCustomer();

            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getPassword(), customer.getPassword()))
                    .thenReturn(false);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerAuthService.login(request, "Login Berhasil")
            );

            assertEquals("Nomor telepon atau password salah", exception.getMessage());
            verify(jwtService, never()).issueCustomer(any(), any());
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("should logout successfully and set logout date")
        void shouldLogoutSuccessfully() {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(PHONE_NUMBER);

            Customer customer = createCustomer();

            when(jwtService.parse(TOKEN)).thenReturn(claims);
            when(jwtService.getExpiration(TOKEN)).thenReturn(EXPIRES_AT);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));

            ResponseEntity<Void> response = customerAuthService.logout(TOKEN, REFRESH_TOKEN);

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNotNull(customer.getLogoutDate());

            verify(customerRepository).save(customer);
            verify(tokenBlacklistService).revoke(TOKEN, EXPIRES_AT);
            verify(customerRefreshTokenService).revoke(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when customer in token is not found")
        void shouldThrowExceptionWhenCustomerNotFoundOnLogout() {
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(UNKNOWN_PHONE);

            when(jwtService.parse(TOKEN)).thenReturn(claims);
            when(jwtService.getExpiration(TOKEN)).thenReturn(EXPIRES_AT);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(UNKNOWN_PHONE))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerAuthService.logout(TOKEN, REFRESH_TOKEN)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
            verify(tokenBlacklistService, never()).revoke(anyString(), any());
        }
    }

    @Nested
    @DisplayName("getCurrentCustomer")
    class GetCurrentCustomerTest {

        @Test
        @DisplayName("should return customer when found by id")
        void shouldReturnCustomerWhenFound() {
            Customer customer = createCustomer();

            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));

            Customer result = customerAuthService.getCurrentCustomer(CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(CUSTOMER_ID, result.getId());
            verify(customerRepository).findByIdAndDeletedDateIsNull(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should throw AuthenticationException when customer not found")
        void shouldThrowExceptionWhenNotFound() {
            when(customerRepository.findByIdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerAuthService.getCurrentCustomer(CUSTOMER_ID)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("register")
    class RegisterTest {

        @Test
        @DisplayName("should register pending customer and generate OTP successfully")
        void shouldRegisterSuccessfully() {
            CustomerRequest request = createCustomerRequest();

            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(request.getPhoneNumber()))
                    .thenReturn(false);
            when(customerRepository.existsByEmailAndDeletedDateIsNull(request.getEmail()))
                    .thenReturn(false);
            when(customerRepository.existsByNationalIdAndDeletedDateIsNull(request.getNationalId()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);

            ResponseEntity<BaseResponse<Void>> response = customerAuthService.register(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(customerRegistrationService).save(any(PendingCustomerRegistration.class));
            verify(otpVerificationService).generate(request.getPhoneNumber());
        }

        @Test
        @DisplayName("should throw ConflictException when phone number already exists")
        void shouldThrowExceptionWhenPhoneExists() {
            CustomerRequest request = createCustomerRequest();

            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(request.getPhoneNumber()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> customerAuthService.register(request)
            );

            assertEquals("Nomor telepon sudah terdaftar", exception.getMessage());
            verify(customerRegistrationService, never()).save(any());
        }

        @Test
        @DisplayName("should throw ConflictException when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            CustomerRequest request = createCustomerRequest();

            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(request.getPhoneNumber()))
                    .thenReturn(false);
            when(customerRepository.existsByEmailAndDeletedDateIsNull(request.getEmail()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> customerAuthService.register(request)
            );

            assertEquals("Email sudah terdaftar", exception.getMessage());
            verify(customerRegistrationService, never()).save(any());
        }

        @Test
        @DisplayName("should throw ConflictException when NIK already exists")
        void shouldThrowExceptionWhenNationalIdExists() {
            CustomerRequest request = createCustomerRequest();

            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(request.getPhoneNumber()))
                    .thenReturn(false);
            when(customerRepository.existsByEmailAndDeletedDateIsNull(request.getEmail()))
                    .thenReturn(false);
            when(customerRepository.existsByNationalIdAndDeletedDateIsNull(request.getNationalId()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> customerAuthService.register(request)
            );

            assertEquals("NIK sudah terdaftar", exception.getMessage());
            verify(customerRegistrationService, never()).save(any());
        }
    }

    @Nested
    @DisplayName("verifyOtp")
    class VerifyOtpTest {

        @Test
        @DisplayName("should verify OTP and complete registration successfully with PENDING verification status")
        void shouldVerifyOtpSuccessfully() {
            VerifyOtpRequest request = new VerifyOtpRequest(PHONE_NUMBER, OTP_CODE);
            PendingCustomerRegistration pending = createPendingRegistration();

            when(customerRegistrationService.get(PHONE_NUMBER))
                    .thenReturn(pending);
            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(false);
            when(customerRepository.existsByEmailAndDeletedDateIsNull(EMAIL))
                    .thenReturn(false);
            when(customerRepository.save(any(Customer.class)))
                    .thenAnswer(invocation -> {
                        Customer c = invocation.getArgument(0);
                        c.setId(CUSTOMER_ID);
                        return c;
                    });
            when(jwtService.issueCustomer(any(Customer.class), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(jwtService.getExpiration(TOKEN))
                    .thenReturn(EXPIRES_AT);
            when(customerRefreshTokenService.create(any(Customer.class)))
                    .thenReturn(REFRESH_TOKEN);

            ResponseEntity<BaseResponse<CustomerAuthResponse>> response =
                    customerAuthService.verifyOtp(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(otpVerificationService).verify(PHONE_NUMBER, OTP_CODE);
            verify(customerRepository).save(argThat(c ->
                    c.getPhoneNumber().equals(PHONE_NUMBER) &&
                            c.getVerificationStatus() == VerificationStatus.PENDING
            ));
            verify(customerRegistrationService).delete(PHONE_NUMBER);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when pending registration is null")
        void shouldThrowExceptionWhenPendingRegistrationNull() {
            VerifyOtpRequest request = new VerifyOtpRequest(PHONE_NUMBER, OTP_CODE);

            when(customerRegistrationService.get(PHONE_NUMBER))
                    .thenReturn(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerAuthService.verifyOtp(request)
            );

            assertEquals("Data registrasi sudah kedaluwarsa", exception.getMessage());
        }

        @Test
        @DisplayName("should throw ConflictException when phone exists on OTP verification")
        void shouldThrowExceptionWhenPhoneExistsOnVerify() {
            VerifyOtpRequest request = new VerifyOtpRequest(PHONE_NUMBER, OTP_CODE);
            PendingCustomerRegistration pending = createPendingRegistration();

            when(customerRegistrationService.get(PHONE_NUMBER)).thenReturn(pending);
            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> customerAuthService.verifyOtp(request)
            );

            assertEquals("Nomor telepon sudah terdaftar", exception.getMessage());
        }

        @Test
        @DisplayName("should throw ConflictException when email exists on OTP verification")
        void shouldThrowExceptionWhenEmailExistsOnVerify() {
            VerifyOtpRequest request = new VerifyOtpRequest(PHONE_NUMBER, OTP_CODE);
            PendingCustomerRegistration pending = createPendingRegistration();

            when(customerRegistrationService.get(PHONE_NUMBER)).thenReturn(pending);
            when(customerRepository.existsByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(false);
            when(customerRepository.existsByEmailAndDeletedDateIsNull(EMAIL))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> customerAuthService.verifyOtp(request)
            );

            assertEquals("Email sudah terdaftar", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("resendOtp")
    class ResendOtpTest {

        @Test
        @DisplayName("should resend OTP successfully when pending registration exists")
        void shouldResendOtpSuccessfully() {
            ResendOtpRequest request = new ResendOtpRequest(PHONE_NUMBER);
            PendingCustomerRegistration pending = createPendingRegistration();

            when(customerRegistrationService.get(PHONE_NUMBER)).thenReturn(pending);

            ResponseEntity<BaseResponse<Void>> response = customerAuthService.resendOtp(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(otpVerificationService).generate(PHONE_NUMBER);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when pending registration not found")
        void shouldThrowExceptionWhenPendingNotFoundOnResend() {
            ResendOtpRequest request = new ResendOtpRequest(PHONE_NUMBER);

            when(customerRegistrationService.get(PHONE_NUMBER)).thenReturn(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerAuthService.resendOtp(request)
            );

            assertEquals("Data registrasi tidak ditemukan atau sudah kedaluwarsa", exception.getMessage());
            verify(otpVerificationService, never()).generate(anyString());
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTest {

        @Test
        @DisplayName("should change password successfully")
        void shouldChangePasswordSuccessfully() {
            ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "NewPass123!");
            Customer customer = createCustomer();

            when(jwtService.getUsername(TOKEN)).thenReturn(PHONE_NUMBER);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword()))
                    .thenReturn(true);
            when(passwordEncoder.matches(request.getNewPassword(), customer.getPassword()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getNewPassword()))
                    .thenReturn("encoded_new_password");

            ResponseEntity<BaseResponse<Void>> response =
                    customerAuthService.changePassword(TOKEN, request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(customerRepository).save(customer);
            assertEquals("encoded_new_password", customer.getPassword());
        }

        @Test
        @DisplayName("should throw RuntimeException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFoundOnPasswordChange() {
            ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "NewPass123!");

            when(jwtService.getUsername(TOKEN)).thenReturn(UNKNOWN_PHONE);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(UNKNOWN_PHONE))
                    .thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> customerAuthService.changePassword(TOKEN, request)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
        }

        @Test
        @DisplayName("should throw RuntimeException when current password is wrong")
        void shouldThrowExceptionWhenCurrentPasswordWrong() {
            ChangePasswordRequest request = new ChangePasswordRequest("WrongPass123!", "NewPass123!");
            Customer customer = createCustomer();

            when(jwtService.getUsername(TOKEN)).thenReturn(PHONE_NUMBER);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword()))
                    .thenReturn(false);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> customerAuthService.changePassword(TOKEN, request)
            );

            assertEquals("Password saat ini salah", exception.getMessage());
        }

        @Test
        @DisplayName("should throw RuntimeException when new password equals old password")
        void shouldThrowExceptionWhenNewPasswordSameAsOld() {
            ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "OldPass123!");
            Customer customer = createCustomer();

            when(jwtService.getUsername(TOKEN)).thenReturn(PHONE_NUMBER);
            when(customerRepository.findByPhoneNumberAndDeletedDateIsNull(PHONE_NUMBER))
                    .thenReturn(Optional.of(customer));
            when(passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword()))
                    .thenReturn(true);
            when(passwordEncoder.matches(request.getNewPassword(), customer.getPassword()))
                    .thenReturn(true);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> customerAuthService.changePassword(TOKEN, request)
            );

            assertEquals("Password baru tidak boleh sama dengan password lama", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTest {

        @Test
        @DisplayName("should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            RefreshTokenRequest request = new RefreshTokenRequest(REFRESH_TOKEN);
            Customer customer = createCustomer();

            CustomerRefreshToken refreshTokenEntity = new CustomerRefreshToken();
            refreshTokenEntity.setCustomer(customer);

            when(customerRefreshTokenService.validate(REFRESH_TOKEN))
                    .thenReturn(refreshTokenEntity);
            when(jwtService.issueCustomer(eq(customer), any(Instant.class)))
                    .thenReturn("new-jwt-token");
            when(jwtService.getExpiration("new-jwt-token"))
                    .thenReturn(EXPIRES_AT);

            ResponseEntity<BaseResponse<CustomerAuthResponse>> response =
                    customerAuthService.refreshToken(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            CustomerAuthResponse authData = response.getBody().getData();
            assertEquals("new-jwt-token", authData.getToken());
            assertEquals(REFRESH_TOKEN, authData.getRefreshToken());
        }
    }

    private CustomerLoginRequest createLoginRequest(String fcmToken) {
        CustomerLoginRequest request = new CustomerLoginRequest();
        request.setPhoneNumber(PHONE_NUMBER);
        request.setPassword(RAW_PASSWORD);
        request.setFcmToken(fcmToken);
        return request;
    }

    private CustomerRequest createCustomerRequest() {
        CustomerRequest request = new CustomerRequest();
        request.setFullName("Bagas");
        request.setNationalId(NATIONAL_ID);
        request.setEmail(EMAIL);
        request.setPhoneNumber(PHONE_NUMBER);
        request.setPassword(RAW_PASSWORD);
        return request;
    }

    private PendingCustomerRegistration createPendingRegistration() {
        return new PendingCustomerRegistration(
                "Bagas",
                NATIONAL_ID,
                EMAIL,
                PHONE_NUMBER,
                ENCODED_PASSWORD
        );
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setCustomerNumber("CUS-123456789012");
        customer.setFullName("Bagas");
        customer.setNationalId(NATIONAL_ID);
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);
        customer.setPassword(ENCODED_PASSWORD);
        customer.setVerificationStatus(VerificationStatus.VERIFIED);
        return customer;
    }
}