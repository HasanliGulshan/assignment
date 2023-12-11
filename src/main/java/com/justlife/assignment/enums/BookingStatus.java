package com.justlife.assignment.enums;

public enum BookingStatus {
    BOOKED("Booked"),
    CANCELED("Canceled"),
    COMPLETED("Completed");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
