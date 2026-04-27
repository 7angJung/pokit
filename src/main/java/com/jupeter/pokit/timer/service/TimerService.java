package com.jupeter.pokit.timer.service;

import com.jupeter.pokit.timer.dto.TimerResultRequest;
import com.jupeter.pokit.timer.entity.StudyRecord;
import com.jupeter.pokit.timer.repository.StudyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final StudyRecordRepository studyRecordRepository;

    // 공부기록 저장
    @Transactional
    public void saveStudyRecord(TimerResultRequest request) {

        if (request.getStudyMinutes() <= 0) {
            throw new IllegalArgumentException("공부시간은 1분 이상이어야 합니다");
        }

        StudyRecord record = StudyRecord.builder()
                .firebaseUid(request.getFirebaseUid())
                .studyMinutes(request.getStudyMinutes())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        studyRecordRepository.save(record);
    }

    // 특정 유저의 전체 공부기록 조회
    public List<StudyRecord> getStudyRecords(String firebaseUid) {
        return studyRecordRepository
                .findByFirebaseUidOrderByCreatedAtDesc(firebaseUid);
    }

    // 특정 유저의 오늘 총 공부시간 조회
    public int getTodayStudyMinutes(String firebaseUid) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<StudyRecord> todayRecords = studyRecordRepository
                .findByFirebaseUidAndStartTimeBetween(
                        firebaseUid, startOfDay, endOfDay);

        return todayRecords.stream()
                .mapToInt(StudyRecord::getStudyMinutes)
                .sum();
    }
}