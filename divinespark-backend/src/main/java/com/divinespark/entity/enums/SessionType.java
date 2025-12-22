package com.divinespark.entity.enums;

public enum SessionType {
    FREE,
    PAID;
    public static SessionType from(String value) {
        return SessionType.valueOf(value.toUpperCase());
    }
}
