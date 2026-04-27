package com.jupeter.pokit.timer.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_records")
@Getter
@NoArgsConstructor
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firebase_uid", nullable = false)
    private String firebaseUid;

    @Column(name = "study_minutes", nullable = false)
    private int studyMinutes;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public StudyRecord(String firebaseUid, int studyMinutes,
                       LocalDateTime startTime, LocalDateTime endTime) {
        this.firebaseUid = firebaseUid;
        this.studyMinutes = studyMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}