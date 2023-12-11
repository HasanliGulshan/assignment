package com.justlife.assignment.controller;

import com.justlife.assignment.model.CleanerResponse;
import com.justlife.assignment.service.CleanerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("cleaners")
public class CleanerController {

    private final CleanerService service;

    public CleanerController(CleanerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CleanerResponse>> getAvailableCleaners(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime time,
                                                                      @RequestParam(required = false) Short duration) {
        return ResponseEntity.ok(service.getAvailableCleaners(date, time, duration));
    }
}
