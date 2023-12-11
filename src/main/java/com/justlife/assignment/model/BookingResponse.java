package com.justlife.assignment.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private long id;
    private byte countOfCleaners;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String vehicleName;
    private long vehicleId;
}
