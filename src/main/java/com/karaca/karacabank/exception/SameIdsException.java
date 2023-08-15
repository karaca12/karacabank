package com.karaca.karacabank.exception;

public class SameIdsException extends RuntimeException {
    public SameIdsException(String message) {
        super(message);
    }
}
