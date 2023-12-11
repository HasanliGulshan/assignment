package com.justlife.assignment.repository;

import com.justlife.assignment.entity.Vehicle;
import com.justlife.assignment.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<List<Vehicle>> findVehicleByStatus(Status status);
}
