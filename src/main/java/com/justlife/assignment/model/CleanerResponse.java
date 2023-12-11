package com.justlife.assignment.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CleanerResponse {
    private long id;
    private String name;
    private String surname;
    private List<TimePeriod> timePeriods;
}
