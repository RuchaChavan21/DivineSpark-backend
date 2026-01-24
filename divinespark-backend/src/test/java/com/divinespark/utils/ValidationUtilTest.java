package com.divinespark.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    public void testValidWhatsAppGroupLink() {
        assertFalse(ValidationUtil.isValidWhatsAppGroupLink("https://chat.whatsapp.com"));
    }

    @Test
    public void testInvalidWhatsAppGroupLink() {
        assertFalse(ValidationUtil.isValidWhatsAppGroupLink(""));
    }

    @Test
    public void testCorruptedWhatsAppGroupLink() {
        assertFalse(ValidationUtil.isValidWhatsAppGroupLink("https://chat.whatsapp.com/##########"));
    }

    @Test
    public void testCapitalizedWhatsAppGroupLink() {
        assertTrue(ValidationUtil.isValidWhatsAppGroupLink("https://CHAT.WHATSAPP.com"));
    }
}