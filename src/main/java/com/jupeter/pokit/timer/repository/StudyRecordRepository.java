package com.jupeter.pokit.timer.repository;

import com.jupeter.pokit.timer.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 특정 유저의 주간 총 공부시간 합산
    @Query("SELECT COALESCE(SUM(s.studyMinutes), 0) FROM StudyRecord s " +
            "WHERE s.firebaseUid = :uid " +
            "AND s.startTime BETWEEN :start AND :end")
    int sumStudyMinutesByFirebaseUidAndStartTimeBetween(
            @Param("uid") String firebaseUid,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}