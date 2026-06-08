package com.company.management.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) { super(message); }
}
