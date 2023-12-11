package com.justlife.assignment.controller;

import com.justlife.assignment.model.CleanerResponse;
import com.justlife.assignment.service.CleanerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CleanerControllerTest {
    @Mock
    private CleanerService cleanerService;

    @InjectMocks
    private CleanerController cleanerController;

    @Test
    void getAvailableCleaners_By_Date() {
        List<CleanerResponse> response = new ArrayList<>();
        LocalDate date = LocalDate.now();
        short duration = 0;

        Mockito.doReturn(response)
                .when(cleanerService)
                .getAvailableCleaners(date, null, duration);

        ResponseEntity<List<CleanerResponse>> actual = cleanerController.getAvailableCleaners(date, null, duration);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
    }

    @Test
    void getAvailableCleaners_By_Date_And_Time() {
        List<CleanerResponse> response = new ArrayList<>();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        short duration = 0;

        Mockito.doReturn(response)
                .when(cleanerService)
                .getAvailableCleaners(date, time, duration);

        ResponseEntity<List<CleanerResponse>> actual = cleanerController.getAvailableCleaners(date, time, duration);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
    }
}
