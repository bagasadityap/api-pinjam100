package com.bagas.pinjam100.service.jwt;

import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.security.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtServiceTest")
class JwtServiceTest {

    private static final String SECRET = "super-secret-key-pinjam100-minimum-256-bits-length-for-hmac-sha!";
    private static final long TTL_MINUTES = 60;
    private static final UUID BRANCH_ID = UUID.randomUUID();

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, TTL_MINUTES);
    }

    @Nested
    @DisplayName("BackOffice User Token Tests")
    class BackOfficeUserTokenTest {

        @Test
        @DisplayName("should issue token with expiry and parse claims correctly")
        void shouldIssueAndParseBackOfficeToken() {
            AppUser user = createAppUser();
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            String token = jwtService.issue(user, now);

            assertNotNull(token);

            Claims claims = jwtService.parse(token);
            assertEquals("ID-12345", claims.getSubject());
            assertEquals("BACK_OFFICE", claims.get("type", String.class));
            assertEquals("ID-12345", claims.get("identityNumber", String.class));
            assertEquals("ADMIN", jwtService.getRole(token));
            assertEquals("ID-12345", jwtService.getUsername(token));

            // Claim branch sekarang berupa ID Cabang
            assertEquals(BRANCH_ID.toString(), claims.get("branch", String.class));

            Instant expectedExpiration = now.plus(Duration.ofMinutes(TTL_MINUTES));
            Instant actualExpiration = jwtService.getExpiration(token);
            assertNotNull(actualExpiration);
            assertEquals(expectedExpiration.getEpochSecond(), actualExpiration.getEpochSecond());
        }

        @Test
        @DisplayName("should issue token without expiry")
        void shouldIssueBackOfficeTokenWithoutExpiry() {
            AppUser user = createAppUser();
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            String token = jwtService.issueWithoutExpiry(user, now);

            assertNotNull(token);
            Claims claims = jwtService.parse(token);
            assertNull(claims.getExpiration());
        }

        @Test
        @DisplayName("should handle user with null role correctly")
        void shouldHandleUserWithNullRole() {
            AppUser user = createAppUser();
            user.setRole(null);
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            String token = jwtService.issue(user, now);

            assertNotNull(token);
            assertNull(jwtService.getRole(token));
        }
    }

    @Nested
    @DisplayName("Customer Token Tests")
    class CustomerTokenTest {

        @Test
        @DisplayName("should issue customer token with expiry and parse claims correctly")
        void shouldIssueAndParseCustomerToken() {
            Customer customer = createCustomer();
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            String token = jwtService.issueCustomer(customer, now);

            assertNotNull(token);

            Claims claims = jwtService.parse(token);
            assertEquals("08123456789", claims.getSubject());
            assertEquals("CUSTOMER", claims.get("type", String.class));
            assertEquals("CUST-001", claims.get("customerNumber", String.class));
            assertEquals("3171234567890001", claims.get("nationalId", String.class));
            assertEquals("Bagas Aditya", claims.get("fullName", String.class));
            assertEquals("bagas@example.com", claims.get("email", String.class));
            assertEquals("08123456789", claims.get("phoneNumber", String.class));
            assertEquals("CUSTOMER", jwtService.getRole(token));

            Instant expectedExpiration = now.plus(Duration.ofMinutes(TTL_MINUTES));
            Instant actualExpiration = jwtService.getExpiration(token);
            assertNotNull(actualExpiration);
            assertEquals(expectedExpiration.getEpochSecond(), actualExpiration.getEpochSecond());
        }

        @Test
        @DisplayName("should issue customer token without expiry")
        void shouldIssueCustomerTokenWithoutExpiry() {
            Customer customer = createCustomer();
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            String token = jwtService.issueCustomerWithoutExpiry(customer, now);

            assertNotNull(token);
            Claims claims = jwtService.parse(token);
            assertNull(claims.getExpiration());
        }
    }

    @Nested
    @DisplayName("Invalid Token Tests")
    class InvalidTokenTest {

        @Test
        @DisplayName("should throw JwtException when parsing token signed with different key")
        void shouldThrowExceptionWhenSignatureIsInvalid() {
            JwtService foreignJwtService = new JwtService("different-secret-key-pinjam100-minimum-256-bits-length!", TTL_MINUTES);
            AppUser user = createAppUser();
            String tokenSignedWithDifferentKey = foreignJwtService.issue(user, Instant.now());

            assertThrows(JwtException.class, () -> jwtService.parse(tokenSignedWithDifferentKey));
        }

        @Test
        @DisplayName("should throw JwtException when parsing malformed token")
        void shouldThrowExceptionWhenTokenIsMalformed() {
            String invalidToken = "invalid.jwt.token";

            assertThrows(JwtException.class, () -> jwtService.parse(invalidToken));
        }
    }

    private AppUser createAppUser() {
        Role role = new Role();
        role.setRoleName("ADMIN");

        // Objek Branch standar
        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setName("Jakarta Pusat");
        branch.setProvince("DKI Jakarta");
        branch.setCity("Jakarta Pusat");
        branch.setPostalCode("10110");

        AppUser user = new AppUser();
        user.setIdUser(UUID.randomUUID());
        user.setIdentityNumber("ID-12345");
        user.setPassword("encodedPassword");
        user.setStatus(true);
        user.setRole(role);
        user.setBranch(branch);
        return user;
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setCustomerNumber("CUST-001");
        customer.setNationalId("3171234567890001");
        customer.setFullName("Bagas Aditya");
        customer.setEmail("bagas@example.com");
        customer.setPhoneNumber("08123456789");
        return customer;
    }
}