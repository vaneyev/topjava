package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFormatter implements Formatter<LocalTime> {

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return StringUtils.hasLength(text) ? LocalTime.parse(text, DateTimeFormatter.ISO_TIME) : null;
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return DateTimeFormatter.ISO_TIME.withLocale(locale).format(object);
    }
}
