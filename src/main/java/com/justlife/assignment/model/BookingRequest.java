package com.justlife.assignment.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequest {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;
    @NotNull
    private Short duration;
    @NotNull
    private byte countOfCleaners;
    @NotNull
    private String customerName;
    @NotNull
    private String customerSurname;
}
