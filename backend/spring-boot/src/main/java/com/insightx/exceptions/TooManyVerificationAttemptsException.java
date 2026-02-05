package com.insightx.exceptions;

public class TooManyVerificationAttemptsException extends InsightXException {
    public TooManyVerificationAttemptsException(String message) {
        super(message);
    }
}
