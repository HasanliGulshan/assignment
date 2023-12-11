package com.justlife.assignment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TimePeriod {
    @DateTimeFormat(pattern = "HH:mm")
    LocalDateTime startTime;
    @DateTimeFormat(pattern = "HH:mm")
    LocalDateTime endTime;

    public TimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
