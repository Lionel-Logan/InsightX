package com.insightx.exceptions;

public class EmailAlreadyExistsException extends InsightXException {
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
