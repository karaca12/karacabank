package com.karaca.karacabank.exception;

public class CustomerIdNotFoundException extends RuntimeException {
    public CustomerIdNotFoundException(String message) {
        super(message);
    }
}
