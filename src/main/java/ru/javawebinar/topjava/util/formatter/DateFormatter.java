package ru.javawebinar.topjava.util.formatter;

import org.springframework.format.Formatter;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter implements Formatter<LocalDate> {

    @Override
    public String print(LocalDate date, Locale locale) {
        return DateTimeFormatter.ISO_DATE.format(date);
    }

    @Override
    public LocalDate parse(String formatted, Locale locale) {
        return DateTimeUtil.parseLocalDate(formatted);
    }
}
