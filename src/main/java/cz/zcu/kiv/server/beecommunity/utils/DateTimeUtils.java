package cz.zcu.kiv.server.beecommunity.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Datetime utils to format and convert date and time
 */
@Slf4j
public class DateTimeUtils {
    private DateTimeUtils(){}
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd-MM-yyyy");

    static Locale czechLocale = Locale.of("cs", "CZ");

    /**
     * Convert string value of date into localdate
     * @param date string value of date
     * @return object of converted localdate
     */
    public static LocalDate getDateFromString(String date) {
        try {
            if (date == null || date.isBlank()) {
                return null;
            }
            formatter = formatter.withLocale(Locale.getDefault());
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException exception) {
            log.warn("Wrong date time format: {}", date);
            return null;
        }
    }

    /**
     * Convert string value of date into date time
     * @param date string value of date
     * @return object of converted localdate
     */
    public static String getDateTimeFromString(LocalDateTime date) {
        try {
            if (date == null) {
                return null;
            }
            formatter = formatter.withLocale(czechLocale);
            return date.format(dateTimeFormatter);
        } catch (DateTimeParseException exception) {
            log.warn("Wrong date time format: {}", date);
            return null;
        }
    }
}
