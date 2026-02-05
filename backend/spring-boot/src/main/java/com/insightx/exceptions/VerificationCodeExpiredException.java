package com.insightx.exceptions;

public class VerificationCodeExpiredException extends InsightXException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}
