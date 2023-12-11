package com.justlife.assignment.service;

import com.justlife.assignment.entity.Booking;
import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.entity.Vehicle;
import com.justlife.assignment.enums.BookingStatus;
import com.justlife.assignment.exception.NotEnoughResourceException;
import com.justlife.assignment.exception.NotWorkingException;
import com.justlife.assignment.model.*;
import com.justlife.assignment.repository.BookingRepository;
import com.justlife.assignment.utility.Utility;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final Utility utility;
    private final CleanerService cleanerService;
    private final BookingRepository bookingRepository;
    private final VehicleService vehicleService;

    public BookingService(Utility utility, CleanerService cleanerService, BookingRepository bookingRepository, VehicleService vehicleService) {
        this.utility = utility;
        this.cleanerService = cleanerService;
        this.bookingRepository = bookingRepository;
        this.vehicleService = vehicleService;
    }


    public BookingResponse createBooking(BookingRequest request) {
            isValidBookingInfo(request);
            LocalDateTime bookingEndTime = utility.calculateEndTime(request.getStartTime(), request.getDuration());

            List<Cleaner> cleaners = cleanerService.getAvailableCleanersByCount(request.getCountOfCleaners());
            if (cleaners.size() < request.getCountOfCleaners()) {

                cleaners.addAll(cleanerService.getBookedCleanersByDateTimeAndCount(request.getStartTime(), bookingEndTime, (short) (request.getCountOfCleaners() - cleaners.size())));
                if (cleaners.size() < request.getCountOfCleaners())
                    throw new NotEnoughResourceException("Sorry, we did our best, but still do not have available cleaners");
            }

            Vehicle vehicle = vehicleService.getAvailableVehicle(request.getStartTime(), request.getDuration(), request.getCountOfCleaners());

            Booking booking = saveBooking(request, cleaners, vehicle, bookingEndTime);

            cleanerService.updateCleaner(cleaners);
            vehicleService.updateVehicle(vehicle);

            return BookingResponse.builder()
                    .id(booking.getId())
                    .vehicleId(booking.getVehicle().getId())
                    .countOfCleaners((byte) booking.getCleaners().size())
                    .startTime(booking.getStartedAt())
                    .endTime(booking.getCompletedAt())
                    .vehicleName(booking.getVehicle().getName())
                    .build();
    }

    public BookingResponse updateBooking(Long bookId, LocalDateTime startTime) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookId);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            BookingRequest request = BookingRequest.builder()
                    .startTime(startTime)
                    .duration(booking.getDuration())
                    .build();

            isValidBookingInfo(request);

            Vehicle previousVehicle = booking.getVehicle();
            LocalDateTime bookingEndTime = utility.calculateEndTime(startTime, booking.getDuration());

            List<Cleaner> cleaners = cleanerService.getAvailableCleanersByCount((short) booking.getCleaners().size());
            if (cleaners.size() < booking.getCleaners().size()) {
                cleaners.addAll(cleanerService.getBookedCleanersByDateTimeAndCount(startTime, bookingEndTime, (short) (booking.getCleaners().size() - cleaners.size())));
            }

            if (cleaners.size() < booking.getCleaners().size())
                throw new NotEnoughResourceException("Sorry we can not update the booking");

            Vehicle vehicle = vehicleService.getAvailableVehicle(startTime, booking.getDuration(), (byte) cleaners.size());

            List<Cleaner> previousCleaners = booking.getCleaners();

            booking.setCleaners(cleaners);
            booking.setStartedAt(startTime);
            booking.setCompletedAt(bookingEndTime);
            booking.setVehicle(vehicle);

            Booking updatedBooking = bookingRepository.save(booking);

            //update assigned vehicle and cleaners
            vehicleService.updateVehicle(vehicle);
            cleanerService.updateCleaner(cleaners);

            //update previous vehicle and cleaners
            vehicleService.updateVehicle(previousVehicle);
            cleanerService.updateCleaner(previousCleaners);

            return BookingResponse.builder()
                    .id(updatedBooking.getId())
                    .vehicleId(updatedBooking.getVehicle().getId())
                    .countOfCleaners((byte) updatedBooking.getCleaners().size())
                    .startTime(updatedBooking.getStartedAt())
                    .endTime(updatedBooking.getCompletedAt())
                    .vehicleName(updatedBooking.getVehicle().getName())
                    .build();
        }

        throw new NotEnoughResourceException("Sorry, we do not have this booking");
    }

    private void isValidBookingInfo(BookingRequest request) {
        if (request.getStartTime().getDayOfWeek() == DayOfWeek.FRIDAY) {
            throw new NotWorkingException("Sorry, we are not working on Friday");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new NotWorkingException("Sorry, we can not make a booking in the past:)");
        }

        if (!(request.getDuration() == 120 || request.getDuration() == 240)) {
            throw new NotWorkingException("Sorry, we can make a booking for 2 or 4 hours");
        }

        LocalDateTime bookingEndTime = utility.calculateEndTime(request.getStartTime(), request.getDuration());
        TimePeriod standartWorkTimePeriod = utility.getStandartWorkTimePeriod(request.getStartTime().toLocalDate());

        if (request.getStartTime().isBefore(standartWorkTimePeriod.getStartTime()) || bookingEndTime.isAfter(standartWorkTimePeriod.getEndTime())) {
            throw new NotWorkingException("Sorry, we are only working from 8:00 to 22:00");
        }

        if (request.getCountOfCleaners() > 3) {
            throw new NotWorkingException("Sorry, we can provide only 3 cleaners");
        }
    }

    private Booking saveBooking(BookingRequest request, List<Cleaner> cleaners, Vehicle vehicle, LocalDateTime bookingEndTime) {
        Booking booking = new Booking();
        booking.setVehicle(vehicle);
        booking.setCleaners(cleaners);
        booking.setStartedAt(request.getStartTime());
        booking.setCompletedAt(bookingEndTime);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setDuration(request.getDuration());
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerSurname(request.getCustomerSurname());

        return bookingRepository.save(booking);
    }
}
