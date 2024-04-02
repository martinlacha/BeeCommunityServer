package cz.zcu.kiv.server.beecommunity.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateTimeUtilsTest {
    @Test
    void testDateFromString_ValidDate() {
        String validDate = "2023-12-31";
        LocalDate result = DateTimeUtils.getDateFromString(validDate);
        assertEquals(LocalDate.of(2023, 12, 31), result);
    }

    @Test
    void testDateFromString_NullOrEmptyDate() {
        String nullDate = null;
        String emptyDate = "";
        LocalDate nullResult = DateTimeUtils.getDateFromString(nullDate);
        LocalDate emptyResult = DateTimeUtils.getDateFromString(emptyDate);
        assertNull(nullResult);
        assertNull(emptyResult);
    }

    @Test
    void testDateFromString_InvalidDate() {
        String invalidDate = "2023-13-31"; // Invalid month
        LocalDate result = DateTimeUtils.getDateFromString(invalidDate);
        assertNull(result);
    }
}
