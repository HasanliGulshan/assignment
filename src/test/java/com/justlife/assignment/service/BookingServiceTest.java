package com.justlife.assignment.service;

import com.justlife.assignment.entity.Booking;
import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.entity.Vehicle;
import com.justlife.assignment.exception.NotEnoughResourceException;
import com.justlife.assignment.exception.NotWorkingException;
import com.justlife.assignment.model.BookingRequest;
import com.justlife.assignment.model.BookingResponse;
import com.justlife.assignment.model.TimePeriod;
import com.justlife.assignment.repository.BookingRepository;
import com.justlife.assignment.utility.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private Utility utility;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private CleanerService cleanerService;
    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_Not_Working_On_Friday() {
        BookingRequest request = BookingRequest.builder()
                .startTime(LocalDateTime.parse("2023-12-15T23:20")).build();
        NotWorkingException exception = assertThrows(NotWorkingException.class,
                () -> bookingService.createBooking(request));

        assertNotNull(exception);
        assertEquals(exception.getErrorMessage(), "Sorry, we are not working on Friday");
    }

    @Test
    void createBooking_Not_Working_Not_Between_Standard_Time() {
        LocalDateTime startTime = LocalDateTime.parse("2023-12-14T07:00");
        short duration = 240;
        BookingRequest request = BookingRequest.builder()
                .startTime(startTime)
                .duration(duration)
                .build();

        LocalDateTime bookingEndTime = LocalDateTime.parse("2023-12-14T09:00");

        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartTime(startTime.toLocalDate().atTime(LocalTime.of(8, 0)));
        timePeriod.setEndTime(startTime.toLocalDate().atTime(LocalTime.of(22, 0)));

        Mockito.doReturn(bookingEndTime)
                .when(utility)
                .calculateEndTime(startTime, duration);

        Mockito.doReturn(timePeriod)
                .when(utility)
                .getStandartWorkTimePeriod(startTime.toLocalDate());

        NotWorkingException exception = assertThrows(NotWorkingException.class,
                () -> bookingService.createBooking(request));

        assertNotNull(exception);
        assertEquals(exception.getErrorMessage(), "Sorry, we are only working from 8:00 to 22:00");
    }

    @Test
    void createBooking_Not_Working_With_More_Than_Three_Cleaners() {
        LocalDateTime startTime = LocalDateTime.parse("2023-12-14T09:00");
        short duration = 240;
        BookingRequest request = BookingRequest.builder()
                .startTime(startTime)
                .duration(duration)
                .countOfCleaners((byte) 4)
                .build();

        LocalDateTime bookingEndTime = LocalDateTime.parse("2023-12-14T11:00");

        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartTime(startTime.toLocalDate().atTime(LocalTime.of(8, 0)));
        timePeriod.setEndTime(startTime.toLocalDate().atTime(LocalTime.of(22, 0)));

        Mockito.doReturn(bookingEndTime)
                .when(utility)
                .calculateEndTime(startTime, duration);

        Mockito.doReturn(timePeriod)
                .when(utility)
                .getStandartWorkTimePeriod(startTime.toLocalDate());

        NotWorkingException exception = assertThrows(NotWorkingException.class,
                () -> bookingService.createBooking(request));

        assertNotNull(exception);
        assertEquals(exception.getErrorMessage(), "Sorry, we can provide only 3 cleaners");
    }

    @Test
    void createBooking() {
        LocalDateTime startTime = LocalDateTime.parse("2023-12-14T09:00");
        short duration = 240;
        byte count = 1;
        long id = 3;
        Cleaner cleaner = new Cleaner();
        List<Cleaner> cleaners = new ArrayList<>();
        cleaners.add(cleaner);
        BookingRequest request = BookingRequest.builder()
                .startTime(startTime)
                .duration(duration)
                .countOfCleaners(count)
                .build();

        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setName("Ali");

        LocalDateTime bookingEndTime = LocalDateTime.parse("2023-12-14T11:00");

        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setStartTime(startTime.toLocalDate().atTime(LocalTime.of(8, 0)));
        timePeriod.setEndTime(startTime.toLocalDate().atTime(LocalTime.of(22, 0)));

        Mockito.doReturn(bookingEndTime)
                .when(utility)
                .calculateEndTime(startTime, duration);

        Mockito.doReturn(timePeriod)
                .when(utility)
                .getStandartWorkTimePeriod(startTime.toLocalDate());

        Mockito.doReturn(cleaners)
                .when(cleanerService)
                .getAvailableCleanersByCount(count);

        Mockito.doReturn(vehicle)
                .when(vehicleService)
                .getAvailableVehicle(startTime, duration, count);

        Mockito.doNothing()
                .when(cleanerService)
                .updateCleaner(cleaners);

        Mockito.doNothing()
                .when(vehicleService)
                .updateVehicle(vehicle);

        Booking booking = new Booking();
        booking.setId(id);
        booking.setStartedAt(startTime);
        booking.setCompletedAt(bookingEndTime);
        booking.setVehicle(vehicle);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);


        BookingResponse actual = bookingService.createBooking(request);

        assertNotNull(actual);
    }

    @Test
    void updateBooking_Not_Valid_BookId() {
        long bookId = 2000;
        LocalDateTime time = LocalDateTime.now();

        Mockito.doReturn(Optional.empty())
                .when(bookingRepository)
                .findById(bookId);

        NotEnoughResourceException exception = assertThrows(NotEnoughResourceException.class,
                () -> bookingService.updateBooking(bookId, time));

        assertNotNull(exception);
        assertEquals(exception.getErrorMessage(), "Sorry, we do not have this booking");
    }
}
