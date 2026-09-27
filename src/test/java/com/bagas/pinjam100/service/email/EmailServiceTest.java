package com.bagas.pinjam100.service.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailServiceTest")
class EmailServiceTest {

    private static final String SENDER_EMAIL = "noreply@pinjam100.com";
    private static final String RECIPIENT_EMAIL = "customer@example.com";
    private static final String RESET_LINK = "https://pinjam100.com/reset-password?token=secret123";

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "from", SENDER_EMAIL);
    }

    @Nested
    @DisplayName("sendPasswordResetEmail")
    class SendPasswordResetEmailTest {

        @Test
        @DisplayName("should create and send email successfully when parameters are valid")
        void shouldSendEmailSuccessfully() {
            MimeMessage mimeMessage = new MimeMessage((Session) null);

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            assertDoesNotThrow(() ->
                    emailService.sendPasswordResetEmail(RECIPIENT_EMAIL, RESET_LINK)
            );

            verify(mailSender).createMimeMessage();
            verify(mailSender).send(mimeMessage);
        }

        @Test
        @DisplayName("should throw IllegalStateException when MessagingException occurs during email construction")
        void shouldThrowExceptionWhenMessagingFails() throws Exception {
            MimeMessage mockMimeMessage = mock(MimeMessage.class);

            when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

            doThrow(new MessagingException("Mail server error"))
                    .when(mockMimeMessage)
                    .setRecipient(any(Message.RecipientType.class), any());

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> emailService.sendPasswordResetEmail(RECIPIENT_EMAIL, RESET_LINK)
            );

            assertEquals("Gagal mengirim email reset password", exception.getMessage());
            verify(mailSender).createMimeMessage();
            verify(mailSender, never()).send(any(MimeMessage.class));
        }
    }
}