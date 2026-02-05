package com.insightx.exceptions;

public class UsernameAlreadyExistsException extends InsightXException {
    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }
}
