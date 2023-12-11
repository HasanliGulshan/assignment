package com.justlife.assignment.service;

import com.justlife.assignment.entity.Booking;
import com.justlife.assignment.entity.Vehicle;
import com.justlife.assignment.enums.Status;
import com.justlife.assignment.exception.NotEnoughResourceException;
import com.justlife.assignment.repository.VehicleRepository;
import com.justlife.assignment.utility.Utility;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final Utility utility;
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository, Utility utility) {
        this.vehicleRepository = vehicleRepository;
        this.utility = utility;
    }

    public Vehicle getAvailableVehicle(LocalDateTime start, short duration, byte count) {
        Optional<List<Vehicle>> availableVehicle = vehicleRepository.findVehicleByStatus(Status.AVAILABLE);

        if (availableVehicle.isPresent()) {
            return availableVehicle.get().get(0);
        } else {
            Optional<List<Vehicle>> fetchedVehicles = vehicleRepository.findVehicleByStatus(Status.BOOKED);

            if (fetchedVehicles.isPresent()) {
                LocalDateTime bookingEndTime = utility.calculateEndTime(start, duration);
                List<Vehicle> bookedVehicles = fetchedVehicles.get();
                for (Vehicle vehicle : bookedVehicles) {
                    List<Booking> bookings = vehicle.getBookings().stream().filter(booking -> booking.getStartedAt().toLocalDate().equals(start.toLocalDate()))
                            .sorted(Comparator.comparing(Booking::getStartedAt)).collect(Collectors.toList());

                    if (!bookings.isEmpty()) {
                        if (bookings.size() == 1) {
                            if (bookings.get(0).getCompletedAt().isBefore(start)) {
                                return bookings.get(0).getVehicle();
                            }
                        } else {
                            for (int i = 0; i < bookings.size() - 1; i++) {
                                if (bookings.get(i).getCompletedAt().isBefore(start) &&
                                        bookings.get(i + 1).getCompletedAt().isAfter(bookingEndTime)
                                        && bookings.get(i).getCleaners().size() + count <= 5) {
                                    return vehicle;
                                }
                            }
                        }
                    } else {
                        return vehicle;
                    }
                }
            }
        }

        throw new NotEnoughResourceException("We do not have available vehicle, sorry");
    }

    protected void updateVehicle(Vehicle vehicle) {
        if (!vehicle.getStatus().equals(Status.BOOKED)) {
            vehicle.setStatus(Status.BOOKED);
        } else if (vehicle.getBookings().size() == 1) {
            vehicle.setStatus(Status.AVAILABLE);
        }

        vehicleRepository.save(vehicle);
    }
}
