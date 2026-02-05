package com.insightx.exceptions;

import java.util.UUID;

public class UserNotFoundException extends InsightXException {
    private final UUID userId;

    public UserNotFoundException(UUID userId) {
        super("User not found with ID: " + userId);
        this.userId = userId;
    }

    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier);
        this.userId = null;
    }

    public UUID getUserId() {
        return userId;
    }
}
