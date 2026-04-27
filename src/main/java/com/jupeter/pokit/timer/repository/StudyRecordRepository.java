package com.jupeter.pokit.timer.repository;

import com.jupeter.pokit.timer.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    // 특정 유저의 전체 공부기록 조회
    List<StudyRecord> findByFirebaseUidOrderByCreatedAtDesc(String firebaseUid);

    // 특정 유저의 특정 기간 공부기록 조회
    List<StudyRecord> findByFirebaseUidAndStartTimeBetween(
            String firebaseUid,
            LocalDateTime start,
            LocalDateTime end
    );
}