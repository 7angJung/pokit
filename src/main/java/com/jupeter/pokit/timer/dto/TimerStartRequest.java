package com.jupeter.pokit.timer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimerStartRequest {

    @NotNull(message = "공부시간은 필수입니다")
    @Min(value = 1, message = "공부시간은 1분 이상이어야 합니다")
    private Integer studyMinutes;

    @NotNull(message = "쉬는시간은 필수입니다")
    @Min(value = 1, message = "쉬는시간은 1분 이상이어야 합니다")
    private Integer breakMinutes;
}