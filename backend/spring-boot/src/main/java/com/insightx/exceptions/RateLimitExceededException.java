package com.insightx.exceptions;

public class RateLimitExceededException extends InsightXException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
