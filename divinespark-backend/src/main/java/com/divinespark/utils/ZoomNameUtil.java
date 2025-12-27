package com.divinespark.utils;

public class ZoomNameUtil {

    public static String getFirstName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Participant";
        }
        return name.trim().split(" ")[0];
    }

    public static String getLastName() {
        return "User"; // constant fallback
    }
}
