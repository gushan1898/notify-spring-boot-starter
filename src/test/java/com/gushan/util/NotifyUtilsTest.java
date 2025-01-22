package com.gushan.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotifyUtilsTest {

    @Test
    void testFormatMessageWithTemplate() {
        String template = "Hello {name}, your order {orderId} is ready";
        String message = NotifyUtils.formatMessage(template, "name", "John", "orderId", "12345");
        
        assertEquals("Hello John, your order 12345 is ready", message);
    }

    @Test
    void testFormatMessageWithMissingParams() {
        String template = "Hello {name}, your order {orderId} is ready";
        String message = NotifyUtils.formatMessage(template, "name", "John");
        
        assertEquals("Hello John, your order {orderId} is ready", message);
    }

    @Test
    void testFormatMessageWithEmptyTemplate() {
        String template = "";
        String message = NotifyUtils.formatMessage(template, "name", "John");
        
        assertEquals("", message);
    }

    @Test
    void testFormatMessageWithNullTemplate() {
        String message = NotifyUtils.formatMessage(null, "name", "John");
        
        assertNull(message);
    }

    @Test
    void testIsValidEmail() {
        assertTrue(NotifyUtils.isValidEmail("test@example.com"));
        assertFalse(NotifyUtils.isValidEmail("invalid-email"));
        assertFalse(NotifyUtils.isValidEmail(null));
    }

    @Test
    void testIsValidPhoneNumber() {
        assertTrue(NotifyUtils.isValidPhoneNumber("+8613812345678"));
        assertFalse(NotifyUtils.isValidPhoneNumber("123456"));
        assertFalse(NotifyUtils.isValidPhoneNumber(null));
    }
}
