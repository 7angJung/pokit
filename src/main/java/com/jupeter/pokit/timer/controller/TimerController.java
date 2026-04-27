package com.jupeter.pokit.timer.controller;

import com.jupeter.pokit.timer.dto.TimerResultRequest;
import com.jupeter.pokit.timer.entity.StudyRecord;
import com.jupeter.pokit.timer.service.TimerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    // 공부기록 저장 (타이머 종료 시)
    @PostMapping("/record")
    public ResponseEntity<String> saveRecord(
            @Valid @RequestBody TimerResultRequest request) {
        try {
            timerService.saveStudyRecord(request);
            return ResponseEntity.ok("공부기록 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 전체 공부기록 조회
    @GetMapping("/records/{firebaseUid}")
    public ResponseEntity<List<StudyRecord>> getRecords(
            @PathVariable String firebaseUid) {
        try {
            List<StudyRecord> records = timerService.getStudyRecords(firebaseUid);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 오늘 총 공부시간 조회
    @GetMapping("/today/{firebaseUid}")
    public ResponseEntity<Integer> getTodayMinutes(
            @PathVariable String firebaseUid) {
        try {
            int minutes = timerService.getTodayStudyMinutes(firebaseUid);
            return ResponseEntity.ok(minutes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}