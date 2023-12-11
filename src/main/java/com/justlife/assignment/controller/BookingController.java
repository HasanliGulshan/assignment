package com.justlife.assignment.controller;


import com.justlife.assignment.model.BookingRequest;
import com.justlife.assignment.model.BookingResponse;
import com.justlife.assignment.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("bookings")
public class BookingController {

    private final BookingService bookingService;

    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
           return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @PutMapping("/{bookId}/{startTime}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long bookId, @PathVariable @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm") LocalDateTime startTime) {
         return ResponseEntity.ok(bookingService.updateBooking(bookId, startTime));
    }
}
