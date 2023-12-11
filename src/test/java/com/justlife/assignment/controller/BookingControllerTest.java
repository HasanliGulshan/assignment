package com.justlife.assignment.controller;

import com.justlife.assignment.model.BookingRequest;
import com.justlife.assignment.model.BookingResponse;
import com.justlife.assignment.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void createBooking() {
        BookingRequest request = BookingRequest.builder()
                .countOfCleaners((byte) 3)
                .duration((short) 120)
                .startTime(LocalDateTime.now())
                .customerName("Gulnur")
                .customerSurname("Hasanli")
                .build();
        BookingResponse response = BookingResponse.builder().build();

        Mockito.doReturn(response)
                .when(bookingService)
                .createBooking(request);

        ResponseEntity<BookingResponse> actual = bookingController.createBooking(request);

        assertNotNull(actual);
        assertEquals(response, actual.getBody());
    }

    @Test
    void updateBooking() {
        long bookId = 7;
        LocalDateTime time = LocalDateTime.now();
        BookingResponse response = BookingResponse.builder()
                .vehicleId(1)
                .endTime(LocalDateTime.now())
                .countOfCleaners((byte) 3)
                .startTime(LocalDateTime.now())
                .build();

        Mockito.doReturn(response)
                .when(bookingService)
                .updateBooking(bookId, time);

        ResponseEntity<BookingResponse> actual = bookingController.updateBooking(bookId, time);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
    }
}
