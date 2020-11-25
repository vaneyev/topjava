package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter implements Formatter<LocalDate> {

    @Override
    public String print(LocalDate date, Locale locale) {
        return DateTimeFormatter.ISO_DATE.withLocale(locale).format(date);
    }

    @Override
    public LocalDate parse(String formatted, Locale locale) throws ParseException {
        return StringUtils.hasLength(formatted) ? LocalDate.parse(formatted, DateTimeFormatter.ISO_DATE) : null;
    }
}
