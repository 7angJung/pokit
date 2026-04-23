package com.jupeter.pokit.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;  // Firebase가 발급한 고유 ID

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public User(String firebaseUid, String email, String nickname) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.nickname = nickname;
    }
}