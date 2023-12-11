package com.justlife.assignment.service;

import com.justlife.assignment.entity.Booking;
import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.entity.Vehicle;
import com.justlife.assignment.enums.Status;
import com.justlife.assignment.exception.NotEnoughResourceException;
import com.justlife.assignment.repository.VehicleRepository;
import com.justlife.assignment.utility.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    @Mock
    private Utility utility;
    @Mock
    private VehicleRepository vehicleRepository;
    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void getAvailableVehicle_Is_Present() {
        LocalDateTime start = LocalDateTime.now();
        short duration = 120;
        byte count = 2;
        Vehicle vehicle = new Vehicle();
        List<Vehicle> list = new ArrayList<>();
        list.add(vehicle);

        Mockito.doReturn(Optional.of(list))
                .when(vehicleRepository)
                .findVehicleByStatus(Status.AVAILABLE);

        Vehicle actual = vehicleService.getAvailableVehicle(start, duration, count);
        assertNotNull(actual);
    }

    @Test
    void getAvailableVehicle_Is_Not_Present() {
        LocalDateTime start = LocalDateTime.parse("2023-12-14T12:00");
        LocalDateTime bookingEndTime = LocalDateTime.parse("2023-12-14T14:00");
        LocalDateTime completedAt = LocalDateTime.parse("2023-12-14T09:00");

        short duration = 120;
        byte count = 2;
        Vehicle vehicle = new Vehicle();
        List<Vehicle> list = new ArrayList<>();
        list.add(vehicle);
        Booking booking = new Booking();

        Cleaner cleaner = new Cleaner();
        List<Cleaner> cleaners = new ArrayList<>();
        cleaners.add(cleaner);

        booking.setStartedAt(start);
        booking.setCompletedAt(completedAt);
        booking.setCleaners(cleaners);
        booking.setVehicle(vehicle);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        vehicle.setBookings(bookings);

        Mockito.doReturn(Optional.empty())
                .when(vehicleRepository)
                .findVehicleByStatus(Status.AVAILABLE);

        Mockito.doReturn(Optional.of(list))
                .when(vehicleRepository)
                .findVehicleByStatus(Status.BOOKED);

        Mockito.doReturn(bookingEndTime)
                .when(utility)
                .calculateEndTime(start, duration);

        Vehicle actual = vehicleService.getAvailableVehicle(start, duration, count);
        assertNotNull(actual);
    }

    @Test
    void getAvailableVehicle_Not_Enough_Resource () {
        LocalDateTime time = LocalDateTime.now();
        short duration = 30;
        byte passengerCount = 3;
        Mockito.doReturn(Optional.empty())
                .when(vehicleRepository)
                .findVehicleByStatus(Status.AVAILABLE);

        NotEnoughResourceException exception = assertThrows(NotEnoughResourceException.class,
                () -> vehicleService.getAvailableVehicle(time, duration, passengerCount));

        assertNotNull(exception);
        assertEquals(exception.getErrorMessage(), "We do not have available vehicle, sorry");
    }
}
