package com.jupeter.pokit.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${app.firebase.credentials}")
    private Resource credentialsResource;

    @Value("${app.firebase.credentials-json:}")
    private String credentialsJson;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credentialsStream;

            if (credentialsJson != null && !credentialsJson.isEmpty()) {
                // 환경변수에서 JSON 직접 읽기 (배포 환경)
                credentialsStream = new ByteArrayInputStream(
                        credentialsJson.getBytes(StandardCharsets.UTF_8));
            } else {
                // 파일에서 읽기 (로컬 환경)
                credentialsStream = credentialsResource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }
}