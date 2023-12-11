package com.justlife.assignment.service;

import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.enums.Status;
import com.justlife.assignment.model.CleanerResponse;
import com.justlife.assignment.repository.CleanerRepository;
import com.justlife.assignment.utility.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CleanerServiceTest {
    @Mock
    private Utility utility;
    @Mock
    private CleanerRepository cleanerRepository;
    @InjectMocks
    private CleanerService cleanerService;

    @Test
    void getBookedCleaners_By_Date_And_Time() {
        LocalDate date = LocalDate.parse("2023-12-15");
        LocalTime time = LocalTime.parse("12:00");
        short duration = 120;
        long id = 9;
        List<Cleaner> cleaners = new ArrayList<>();
        Cleaner cleaner = new Cleaner();
        cleaner.setId(id);
        cleaners.add(cleaner);

        LocalDateTime cleaningStartTime = date.atTime(time);
        LocalDateTime cleaningEndTime = date.atTime(LocalTime.parse("14:00"));

        Mockito.doReturn(cleaningEndTime)
                .when(utility)
                .calculateEndTime(cleaningStartTime, duration);

        Mockito.doReturn(Optional.of(cleaners))
                .when(cleanerRepository)
                .findCleanersByStatus(Status.BOOKED);

        List<CleanerResponse> responses = new ArrayList<>();

        List<CleanerResponse> actual = cleanerService.getBookedCleanersByDateAndTime(date, time, duration, responses);

        assertNotNull(actual);
    }

    @Test
    void getBookedCleaners_By_Date_And_Time_And_Count() {
        LocalDate date = LocalDate.parse("2023-12-15");
        LocalTime time = LocalTime.parse("12:00");
        short duration = 120;
        long id = 9;
        short count = 1;
        List<Cleaner> cleaners = new ArrayList<>();
        Cleaner cleaner = new Cleaner();
        cleaner.setId(id);
        cleaners.add(cleaner);

        LocalDateTime cleaningStartTime = date.atTime(time);
        LocalDateTime cleaningEndTime = date.atTime(LocalTime.parse("14:00"));

        Mockito.doReturn(Optional.of(cleaners))
                .when(cleanerRepository)
                .findCleanersByStatus(Status.BOOKED);

        List<Cleaner> actual = cleanerService.getBookedCleanersByDateTimeAndCount(cleaningStartTime, cleaningEndTime, count);

        assertNotNull(actual);
        assertEquals(actual.size(), count);
    }

    @Test
    void getAvailableCleaners_By_Count() {
        short count = 3;
        Cleaner cleaner = new Cleaner();
        List<Cleaner> cleaners = new ArrayList<>();
        cleaners.add(cleaner);

        Mockito.doReturn(Optional.of(cleaners))
                .when(cleanerRepository)
                .findCleanersByStatusAndCount(Status.AVAILABLE.toString(), count);

        List<Cleaner> actual = cleanerService.getAvailableCleanersByCount(count);

        assertNotNull(actual);
    }
}
