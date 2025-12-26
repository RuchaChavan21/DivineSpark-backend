package com.divinespark.utils;

public class ZoomUtils {

    public static String extractMeetingId(String zoomLink) {
        if (zoomLink == null) return null;

        String[] parts = zoomLink.split("/j/");
        if (parts.length < 2) return null;

        return parts[1].split("\\?")[0];
    }

}
