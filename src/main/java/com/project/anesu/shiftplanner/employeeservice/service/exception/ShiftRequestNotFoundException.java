package com.project.anesu.shiftplanner.employeeservice.service.exception;

public class ShiftRequestNotFoundException extends RuntimeException {
    public ShiftRequestNotFoundException(String message) {
        super(message);
    }
}
