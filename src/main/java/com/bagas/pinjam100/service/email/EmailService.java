package com.bagas.pinjam100.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendPasswordResetEmail(String email, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Permintaan Reset Password - Pinjam100");

            String html = """
                <!DOCTYPE html>
                <html lang="id">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; background-color: #f4f6f9; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; -webkit-font-smoothing: antialiased;">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="table-layout: fixed;">
                        <tr>
                            <td align="center" style="padding: 40px 10px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 520px; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e5e7eb;">

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 32px;">
                                            <h2 style="margin: 0 0 12px 0; color: #1f2937; font-size: 20px; font-weight: 700; text-align: center;">
                                                Reset Password Akun Anda
                                            </h2>

                                            <p style="margin: 0 0 16px 0; color: #4b5563; font-size: 14px; line-height: 1.6; text-align: center;">
                                                Halo, kami menerima permintaan untuk mengatur ulang kata sandi akun <strong>Pinjam100</strong> Anda.
                                            </p>

                                            <!-- Call to Action Button -->
                                            <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="margin: 24px 0;">
                                                <tr>
                                                    <td align="center">
                                                        <a href="%s" target="_blank" style="display: inline-block; padding: 14px 28px; background-color: #0E209C; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px; border-radius: 10px; box-shadow: 0 2px 4px rgba(14, 32, 156, 0.25);">
                                                            Atur Ulang Password
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <!-- Info Validity Box -->
                                            <div style="background-color: #f8fafc; border-left: 4px solid #0E209C; padding: 12px 16px; border-radius: 6px; margin-bottom: 20px;">
                                                <p style="margin: 0; color: #64748b; font-size: 12px; line-height: 1.5;">
                                                    ⏱️ Tautan ini berlaku selama <strong>15 menit</strong> dan hanya dapat digunakan 1 kali.
                                                </p>
                                            </div>

                                            <p style="margin: 0 0 20px 0; color: #6b7280; font-size: 13px; line-height: 1.5;">
                                                Jika tombol di atas tidak bekerja, silakan salin dan tempel tautan berikut ke browser Anda:
                                            </p>

                                            <p style="margin: 0 0 24px 0; word-break: break-all; font-size: 12px; color: #2563eb;">
                                                <a href="%s" style="color: #2563eb; text-decoration: underline;">%s</a>
                                            </p>

                                            <hr style="border: none; border-top: 1px solid #f3f4f6; margin: 20px 0;" />

                                            <p style="margin: 0; color: #9ca3af; font-size: 12px; line-height: 1.5; text-align: center;">
                                                Jika Anda tidak merasa melakukan permintaan ini, abaikan email ini secara aman. Kata sandi Anda tidak akan berubah.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f9fafb; padding: 16px 32px; text-align: center; border-top: 1px solid #f3f4f6;">
                                            <p style="margin: 0; color: #9ca3af; font-size: 11px;">
                                                &copy; Pinjam100. Seluruh hak cipta dilindungi undang-undang.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(resetLink, resetLink, resetLink);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException(
                    "Gagal mengirim email reset password",
                    e
            );
        }
    }
}