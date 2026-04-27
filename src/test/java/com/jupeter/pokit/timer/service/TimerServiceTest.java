package com.jupeter.pokit.timer.service;

import com.jupeter.pokit.timer.dto.TimerResultRequest;
import com.jupeter.pokit.timer.entity.StudyRecord;
import com.jupeter.pokit.timer.repository.StudyRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TimerServiceTest {

    @InjectMocks
    private TimerService timerService;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @Test
    @DisplayName("공부기록 저장 성공")
    void saveStudyRecord_success() {

        // given
        TimerResultRequest request = new TimerResultRequest();
        request.setFirebaseUid("firebase-uid-123");
        request.setStudyMinutes(50);
        request.setStartTime(LocalDateTime.now().minusMinutes(50));
        request.setEndTime(LocalDateTime.now());

        // when
        timerService.saveStudyRecord(request);

        // then
        verify(studyRecordRepository, times(1)).save(any(StudyRecord.class));
    }

    @Test
    @DisplayName("전체 공부기록 조회 성공")
    void getStudyRecords_success() {

        // given
        String firebaseUid = "firebase-uid-123";
        StudyRecord mockRecord = StudyRecord.builder()
                .firebaseUid(firebaseUid)
                .studyMinutes(50)
                .startTime(LocalDateTime.now().minusMinutes(50))
                .endTime(LocalDateTime.now())
                .build();

        when(studyRecordRepository.findByFirebaseUidOrderByCreatedAtDesc(firebaseUid))
                .thenReturn(List.of(mockRecord));

        // when
        List<StudyRecord> records = timerService.getStudyRecords(firebaseUid);

        // then
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertEquals(firebaseUid, records.get(0).getFirebaseUid());
    }

    @Test
    @DisplayName("오늘 총 공부시간 합산 성공")
    void getTodayStudyMinutes_success() {

        // given
        String firebaseUid = "firebase-uid-123";
        StudyRecord record1 = StudyRecord.builder()
                .firebaseUid(firebaseUid)
                .studyMinutes(50)
                .startTime(LocalDateTime.now().minusHours(3))
                .endTime(LocalDateTime.now().minusHours(2))
                .build();
        StudyRecord record2 = StudyRecord.builder()
                .firebaseUid(firebaseUid)
                .studyMinutes(30)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now())
                .build();

        when(studyRecordRepository.findByFirebaseUidAndStartTimeBetween(
                eq(firebaseUid), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(record1, record2));

        // when
        int totalMinutes = timerService.getTodayStudyMinutes(firebaseUid);

        // then
        assertEquals(80, totalMinutes);  // 50 + 30 = 80분
    }

    @Test
    @DisplayName("공부시간 0분 이하 저장 시도 시 예외 발생")
    void saveStudyRecord_fail_invalidMinutes() {

        // given
        TimerResultRequest request = new TimerResultRequest();
        request.setFirebaseUid("firebase-uid-123");
        request.setStudyMinutes(0);  // 0분
        request.setStartTime(LocalDateTime.now().minusMinutes(50));
        request.setEndTime(LocalDateTime.now());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> timerService.saveStudyRecord(request));

        verify(studyRecordRepository, never()).save(any(StudyRecord.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저 기록 조회 시 빈 리스트 반환")
    void getStudyRecords_emptyList() {

        // given
        String firebaseUid = "non-existent-uid";
        when(studyRecordRepository.findByFirebaseUidOrderByCreatedAtDesc(firebaseUid))
                .thenReturn(Collections.emptyList());

        // when
        List<StudyRecord> records = timerService.getStudyRecords(firebaseUid);

        // then
        assertTrue(records.isEmpty());
    }

    @Test
    @DisplayName("주간 총 공부시간 합산 성공")
    void getWeeklyStudyMinutes_success() {

        // given
        String firebaseUid = "firebase-uid-123";
        when(studyRecordRepository.sumStudyMinutesByFirebaseUidAndStartTimeBetween(
                eq(firebaseUid), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(150);

        // when
        int totalMinutes = timerService.getWeeklyStudyMinutes(firebaseUid);

        // then
        assertEquals(150, totalMinutes);
    }

    @Test
    @DisplayName("월간 총 공부시간 합산 성공")
    void getMonthlyStudyMinutes_success() {

        // given
        String firebaseUid = "firebase-uid-123";
        when(studyRecordRepository.sumStudyMinutesByFirebaseUidAndStartTimeBetween(
                eq(firebaseUid), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(600);

        // when
        int totalMinutes = timerService.getMonthlyStudyMinutes(firebaseUid);

        // then
        assertEquals(600, totalMinutes);
    }

    @Test
    @DisplayName("주간 공부기록 없을 때 0 반환")
    void getWeeklyStudyMinutes_empty() {

        // given
        String firebaseUid = "firebase-uid-123";
        when(studyRecordRepository.sumStudyMinutesByFirebaseUidAndStartTimeBetween(
                eq(firebaseUid), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0);

        // when
        int totalMinutes = timerService.getWeeklyStudyMinutes(firebaseUid);

        // then
        assertEquals(0, totalMinutes);
    }

    @Test
    @DisplayName("월간 공부기록 없을 때 0 반환")
    void getMonthlyStudyMinutes_empty() {

        // given
        String firebaseUid = "firebase-uid-123";
        when(studyRecordRepository.sumStudyMinutesByFirebaseUidAndStartTimeBetween(
                eq(firebaseUid), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0);

        // when
        int totalMinutes = timerService.getMonthlyStudyMinutes(firebaseUid);

        // then
        assertEquals(0, totalMinutes);
    }
}