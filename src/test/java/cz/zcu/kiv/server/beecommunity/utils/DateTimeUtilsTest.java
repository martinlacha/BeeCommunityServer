package cz.zcu.kiv.server.beecommunity.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateTimeUtilsTest {
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd-MM-yyyy");

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
        String invalidDate = "2023-13-31";
        LocalDate result = DateTimeUtils.getDateFromString(invalidDate);
        assertNull(result);
    }
    @Test
    void testValidDateTimeConversion() {
        LocalDateTime inputDateTime = LocalDateTime.of(2022, 1, 1, 12, 0);
        String expectedOutput = "12:00:00 01-01-2022";
        String actualOutput = DateTimeUtils.getDateTimeFromString(inputDateTime);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testNullDateTime() {
        LocalDateTime inputDateTime = null;
        String actualOutput = DateTimeUtils.getDateTimeFromString(inputDateTime);
        assertNull(actualOutput);
    }
}