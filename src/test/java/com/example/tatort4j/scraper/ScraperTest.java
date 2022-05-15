package com.example.tatort4j.scraper;

import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ScraperTest {

    @Test
    void parseCurrentWebsite() {
        final Scraper s = new Scraper();
        var broadcasts = s.ParseTatortWebsite();
        assertEquals(5, (long) broadcasts.size());
    }

    @Test
    void parseTatortWebsite() {
        try {
            String website = Files.readString(Path.of("src/test/resources/scraper/websites/20210209.html"));
            var result = Scraper.parseHtml(website, Scraper.ScheduleType.ERSTE);
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/scraper/row_test.csv", numLinesToSkip = 1, delimiter = ';')
    void parseRow(String row, String expTitle, String expCity, String expInspectors, String expTime, String expChannel, String requestTime) {
        Scraper.ScheduleType channel = expChannel.equals("Erste") ? Scraper.ScheduleType.ERSTE : Scraper.ScheduleType.DRITTE;
        var b = Scraper.parseRow(row,
                ZonedDateTime.parse(requestTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),
                channel);
        assertEquals(expTitle, b.title);
        assertEquals(expCity, b.city);
        assertEquals(expInspectors, b.inspectors);
        assertEquals(expTime, b.time);
        if (!expChannel.equals("Erste")) {
            assertEquals(expChannel, b.channel);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/scraper/datetime_test.csv", numLinesToSkip = 1, delimiter = ';')
    void parseDateTime(String day, String time, String requestTime, String expected) {
        var r = ZonedDateTime.parse(requestTime);
        var broadcast = Scraper.parseDatetime(day, time, r);

        var dateString = broadcast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertEquals(expected, dateString);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/scraper/title_test.csv", numLinesToSkip = 1, delimiter = ';')
    void parseTitle(String input, String title, String inspectors, String city) {
        var t = Scraper.parseTitle(input);
        assertEquals(title, t.title);
        assertEquals(inspectors, t.inspectors);
        assertEquals(city, t.city);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/scraper/requestdate_test.csv", numLinesToSkip = 1, delimiter = ';')
    void parseRequestTime(String comment, String expected) {
        final ZonedDateTime rt = Scraper.parseRequestTime(comment);
        final String formatted = rt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        assertEquals(expected, formatted);
    }
}