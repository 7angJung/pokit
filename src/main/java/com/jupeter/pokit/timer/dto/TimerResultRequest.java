package com.jupeter.pokit.timer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TimerResultRequest {

    @NotNull(message = "Firebase UID는 필수입니다")
    private String firebaseUid;

    @NotNull(message = "공부시간은 필수입니다")
    @Min(value = 1, message = "공부시간은 1분 이상이어야 합니다")
    private Integer studyMinutes;

    @NotNull(message = "시작 시간은 필수입니다")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    private LocalDateTime endTime;
}