package com.bagas.pinjam100.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    public FirebaseConfig() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

            if (credentialsPath == null || credentialsPath.isBlank()) {
                throw new IOException(
                        "GOOGLE_APPLICATION_CREDENTIALS environment variable is not set"
                );
            }

            try (FileInputStream serviceAccount =
                         new FileInputStream(credentialsPath)) {

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        }
    }
}