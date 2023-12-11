package com.justlife.assignment.repository;

import com.justlife.assignment.entity.Cleaner;
import com.justlife.assignment.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
    Optional<List<Cleaner>> findCleanersByStatus(Status status);
    @Query(value = "select * from cleaner c where c.status=:status limit :count", nativeQuery = true)
    Optional<List<Cleaner>> findCleanersByStatusAndCount(@Param("status") String status, @Param("count") short count);
}
