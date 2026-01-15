package com.divinespark.utils;

public class ValidationUtil {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidWhatsAppGroupLink(String link) {
        if (isBlank(link)) return false;

        String l = link.trim().toLowerCase();
        // WhatsApp group invite links usually look like:
        // https://chat.whatsapp.com/<code>
        // https://www.whatsapp.com/invite/<code>
        return l.startsWith("https://chat.whatsapp.com/")
                || l.startsWith("https://www.whatsapp.com/invite/");
    }
}
