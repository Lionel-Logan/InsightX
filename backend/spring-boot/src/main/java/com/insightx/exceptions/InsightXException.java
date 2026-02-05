package com.insightx.exceptions;

/**
 * Base exception for all InsightX application exceptions
 */
public class InsightXException extends RuntimeException {
    public InsightXException(String message) {
        super(message);
    }

    public InsightXException(String message, Throwable cause) {
        super(message, cause);
    }
}
