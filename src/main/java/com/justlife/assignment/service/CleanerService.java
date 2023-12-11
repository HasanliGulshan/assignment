package com.justlife.assignment.service;

import com.justlife.assignment.entity.Booking;
import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.enums.Status;
import com.justlife.assignment.model.CleanerResponse;
import com.justlife.assignment.model.TimePeriod;
import com.justlife.assignment.repository.CleanerRepository;
import com.justlife.assignment.utility.Utility;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CleanerService {
    private final CleanerRepository cleanerRepository;

    private final Utility utility;

    CleanerService(CleanerRepository cleanerRepository, Utility utility) {
        this.cleanerRepository = cleanerRepository;
        this.utility = utility;
    }

    public List<CleanerResponse> getAvailableCleaners(LocalDate date, LocalTime time, Short duration) {
        if (time != null) {
            if (duration != null) {
                return getAvailableCleanersByDateAndTime(date, time, duration);
            }
            throw new ValidationException("duration field should not be null");
        } else {
            return getAvailableCleanersByDate(date);
        }
    }

    private List<CleanerResponse> getAvailableCleanersByDateAndTime(LocalDate date, LocalTime time, short duration) {
        List<CleanerResponse> cleanerResponse = getAvailableCleaners(date);
        return getBookedCleanersByDateAndTime(date, time, duration, cleanerResponse);
    }

    private List<CleanerResponse> getAvailableCleanersByDate(LocalDate date) {
        List<CleanerResponse> cleanerResponse = getAvailableCleaners(date);
        return getBookedCleanersByDate(date, cleanerResponse);
    }

    private List<CleanerResponse> getAvailableCleaners(LocalDate date) {
        Optional<List<Cleaner>> availableCleaners = cleanerRepository.findCleanersByStatus(Status.AVAILABLE);
        return utility.convertCleanerToDto(availableCleaners, date);
    }

    protected List<Cleaner> getAvailableCleanersByCount(short count) {
        Optional<List<Cleaner>> availableCleaners = cleanerRepository.findCleanersByStatusAndCount(Status.AVAILABLE.toString(), count);
        return availableCleaners.orElseGet(ArrayList::new);
    }

    private List<CleanerResponse> getBookedCleanersByDate(LocalDate date, List<CleanerResponse> cleanerResponse) {
        Optional<List<Cleaner>> bookedCleaners = cleanerRepository.findCleanersByStatus(Status.BOOKED);

        if (bookedCleaners.isPresent()) {
            for (Cleaner cleaner : bookedCleaners.get()) {
                List<TimePeriod> timePeriods = new ArrayList<>();
                LocalDateTime startTime = date.atTime(LocalTime.parse("08:00"));

                List<Booking> bookingListForSearchedDate = cleaner.getBookings().stream().filter(booking -> booking.getStartedAt().toLocalDate().equals(date))
                        .sorted(Comparator.comparing(Booking::getStartedAt)).collect(Collectors.toList());

                if (!bookingListForSearchedDate.isEmpty()) {
                    for (Booking booking : bookingListForSearchedDate) {
                        TimePeriod timePeriod = new TimePeriod();
                        if (startTime.isBefore(booking.getStartedAt())) {
                            timePeriod.setStartTime(startTime);
                            timePeriod.setEndTime(booking.getStartedAt());

                            startTime = booking.getCompletedAt().plusMinutes(30);
                            timePeriods.add(timePeriod);
                        } else if (startTime.isEqual(booking.getStartedAt())) {
                            startTime = booking.getCompletedAt().plusMinutes(30);
                        }
                    }

                    LocalDateTime end = date.atTime(LocalTime.parse("22:00"));
                    int size = bookingListForSearchedDate.size() - 1;
                    LocalDateTime lastBookingCompletedTime = bookingListForSearchedDate.get(size).getCompletedAt();

                    if (lastBookingCompletedTime.plusMinutes(30).isBefore(end)) {
                        timePeriods.add(new TimePeriod(lastBookingCompletedTime.plusMinutes(30), end));
                    }
                } else {
                    TimePeriod timePeriod = utility.getStandartWorkTimePeriod(date);
                    timePeriods.add(timePeriod);
                }

                CleanerResponse response = CleanerResponse.builder()
                        .id(cleaner.getId())
                        .surname(cleaner.getSurname())
                        .name(cleaner.getName())
                        .timePeriods(timePeriods)
                        .build();

                cleanerResponse.add(response);
            }
        }
        return cleanerResponse;
    }

    protected List<CleanerResponse> getBookedCleanersByDateAndTime(LocalDate date, LocalTime time, short duration, List<CleanerResponse> cleanerResponse) {
        LocalDateTime dayStartTime = date.atTime(LocalTime.parse("08:00"));
        LocalDateTime dayEndTime = date.atTime(LocalTime.parse("22:00"));

        LocalDateTime cleaningStartTime = date.atTime(time);
        LocalDateTime cleaningEndTime = utility.calculateEndTime(cleaningStartTime, duration);

        Optional<List<Cleaner>> bookedCleaners = cleanerRepository.findCleanersByStatus(Status.BOOKED);

        if (bookedCleaners.isPresent()) {
            for (Cleaner cleaner : bookedCleaners.get()) {
                List<TimePeriod> timePeriods = new ArrayList<>();

                List<Booking> bookingsForSearchedDate = cleaner.getBookings().stream().filter
                                (booking -> booking.getStartedAt().toLocalDate().equals(date))
                        .sorted(Comparator.comparing(Booking::getStartedAt)).collect(Collectors.toList());

                if (!bookingsForSearchedDate.isEmpty()) {
                    int lastIndex = bookingsForSearchedDate.size() - 1;
                    LocalDateTime firstBookingStartTime = bookingsForSearchedDate.get(0).getStartedAt();
                    LocalDateTime lastBookingCompletedTime = bookingsForSearchedDate.get(lastIndex).getCompletedAt();

                    if (!cleaningStartTime.isBefore(dayStartTime) && (!cleaningEndTime.plusMinutes(30).isAfter(firstBookingStartTime))) {

                        TimePeriod timePeriod = utility.getRequiredTimePeriod(cleaningStartTime, cleaningEndTime);
                        timePeriods.add(timePeriod);
                    } else if (!lastBookingCompletedTime.plusMinutes(30).isAfter(cleaningStartTime)
                            && !cleaningEndTime.isAfter(dayEndTime)) {

                        TimePeriod timePeriod = utility.getRequiredTimePeriod(cleaningStartTime, cleaningEndTime);
                        timePeriods.add(timePeriod);
                    } else {
                        for (int i = 0; i < bookingsForSearchedDate.size() - 1; i++) {
                            if (bookingsForSearchedDate.get(i).getCompletedAt().plusMinutes(30).isBefore(cleaningStartTime)
                                    && bookingsForSearchedDate.get(i + 1).getStartedAt().isAfter(cleaningEndTime.plusMinutes(30))) {
                                TimePeriod timePeriod = new TimePeriod();

                                timePeriod.setStartTime(cleaningStartTime);
                                timePeriod.setEndTime(cleaningEndTime);
                                timePeriods.add(timePeriod);
                            }
                        }
                    }
                } else {
                    TimePeriod timePeriod = utility.getRequiredTimePeriod(cleaningStartTime, cleaningEndTime);
                    timePeriods.add(timePeriod);
                }

                CleanerResponse response = CleanerResponse.builder()
                        .id(cleaner.getId())
                        .name(cleaner.getName())
                        .surname(cleaner.getSurname())
                        .timePeriods(timePeriods)
                        .build();

                cleanerResponse.add(response);
            }
        }
        return cleanerResponse;
    }

    protected List<Cleaner> getBookedCleanersByDateTimeAndCount(LocalDateTime cleaningStartTime, LocalDateTime cleaningEndTime, short count) {
        List<Cleaner> cleaners = new ArrayList<>();

        LocalDateTime dayStartTime = cleaningStartTime.toLocalDate().atTime(LocalTime.parse("08:00"));
        LocalDateTime dayEndTime = cleaningEndTime.toLocalDate().atTime(LocalTime.parse("22:00"));

        Optional<List<Cleaner>> fetchedCleaners = cleanerRepository.findCleanersByStatus(Status.BOOKED);

        if (fetchedCleaners.isPresent()) {
            List<Cleaner> bookedCleaners = fetchedCleaners.get();
            for (Cleaner cleaner : bookedCleaners) {

                List<Booking> bookingListForCleaningDate = cleaner.getBookings().stream().filter(booking -> booking.getStartedAt().toLocalDate().equals(cleaningStartTime.toLocalDate()))
                        .sorted(Comparator.comparing(Booking::getStartedAt)).collect(Collectors.toList());
                //solve edge cases -  start and end date might be at 8:00 or 22:00, there might be no booking, so we need to check edge cases also

                if (!bookingListForCleaningDate.isEmpty()) {
                    int lastIndex = bookingListForCleaningDate.size() - 1;
                    LocalDateTime firstBookingStartTime = bookingListForCleaningDate.get(0).getStartedAt();
                    LocalDateTime lastBookingCompletedTime = bookingListForCleaningDate.get(lastIndex).getCompletedAt();

                    if (!cleaningStartTime.isBefore(dayStartTime) && (!cleaningEndTime.plusMinutes(30).isAfter(firstBookingStartTime))) {
                        cleaners.add(cleaner);
                    } else if (!lastBookingCompletedTime.plusMinutes(30).isAfter(cleaningStartTime) && !cleaningEndTime.isAfter(dayEndTime)) {
                        cleaners.add(cleaner);
                    } else {
                        for (int i = 0; i < bookingListForCleaningDate.size() - 1; i++) {
                            if (bookingListForCleaningDate.get(i).getCompletedAt().plusMinutes(30).isBefore(cleaningStartTime)
                                    && bookingListForCleaningDate.get(i + 1).getStartedAt().isAfter(cleaningEndTime.plusMinutes(30))) {
                                if (cleaners.size() == count) break;

                                if (!cleaners.contains(cleaner)) {
                                    cleaners.add(cleaner);
                                }
                            }
                        }
                    }
                } else {
                    cleaners.add(cleaner);
                }

                if (cleaners.size() == count) break;
            }
        }
        return cleaners;
    }

    protected void updateCleaner(List<Cleaner> cleaners) {
        for (Cleaner cleaner : cleaners) {
            if (!cleaner.getStatus().equals(Status.BOOKED)) {
                cleaner.setStatus(Status.BOOKED);
            } else if (cleaner.getBookings().isEmpty()) {
                cleaner.setStatus(Status.AVAILABLE);
            }

            cleanerRepository.save(cleaner);
        }
    }
}
