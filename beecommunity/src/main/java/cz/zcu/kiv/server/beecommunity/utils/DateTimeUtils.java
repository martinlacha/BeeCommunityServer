package cz.zcu.kiv.server.beecommunity.utils;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Slf4j
public class DateTimeUtils {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate getDateFromString(String date) {
        try {
            if (date == null || date.isBlank()) {
                return null;
            }
            formatter = formatter.withLocale(Locale.getDefault());  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException exception) {
            log.warn("Wrong date time format: {}", date);
            return null;
        }
    }
}
