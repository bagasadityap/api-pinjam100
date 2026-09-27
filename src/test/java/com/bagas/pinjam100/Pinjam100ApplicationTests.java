package com.bagas.pinjam100;

import com.bagas.pinjam100.service.notification.FirebaseNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class Pinjam100ApplicationTests {

    @MockitoBean
    FirebaseNotificationService firebaseNotificationService;

    @Test
    void contextLoads() {
    }
}