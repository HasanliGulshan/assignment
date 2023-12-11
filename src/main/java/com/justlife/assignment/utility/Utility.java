package com.justlife.assignment.utility;

import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.model.CleanerResponse;
import com.justlife.assignment.model.TimePeriod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Utility {

    public List<CleanerResponse> convertCleanerToDto(Optional<List<Cleaner>> availableCleaners, LocalDate date) {
        List<CleanerResponse> responseList = new ArrayList<>();

        if (availableCleaners.isPresent()) {
            TimePeriod timePeriod = getStandartWorkTimePeriod(date);
            List<TimePeriod> timePeriods = new ArrayList<>();
            timePeriods.add(timePeriod);

            for (Cleaner cleaner : availableCleaners.get()) {

                CleanerResponse response = CleanerResponse.builder()
                        .id(cleaner.getId())
                        .surname(cleaner.getSurname())
                        .name(cleaner.getName())
                        .timePeriods(timePeriods)
                        .build();

                responseList.add(response);
            }
        }
        return responseList;
    }

    public TimePeriod getStandartWorkTimePeriod(LocalDate date) {
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartTime(date.atTime(LocalTime.of(8, 0)));
        timePeriod.setEndTime(date.atTime(LocalTime.of(22, 0)));
        return timePeriod;
    }

    public TimePeriod getRequiredTimePeriod(LocalDateTime from, LocalDateTime to) {
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartTime(from);
        timePeriod.setEndTime(to);
        return timePeriod;
    }

    public LocalDateTime calculateEndTime(LocalDateTime startTime, short duration) {
        int hours = duration / 60;
        int minutes = duration - hours * 60;
        return startTime.plusHours(hours).plusMinutes(minutes);
    }
}
