package com.karaca.karacabank.exception;

public class AccountIdNotFoundException extends RuntimeException {
    public AccountIdNotFoundException(String message) {
        super(message);
    }
}
