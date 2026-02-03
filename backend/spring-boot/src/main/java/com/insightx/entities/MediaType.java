package com.insightx.entities;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * MediaType Enum - Defines supported media types in InsightX
 */
public enum MediaType {
    MOVIE("movie"),
    BOOK("book"),
    GAME("game");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static MediaType fromValue(String value) {
        for (MediaType type : MediaType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid media type: " + value);
    }
}