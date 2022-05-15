package com.example.tatort4j.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {
    final String TATORT_URL = "https://www.daserste.de/unterhaltung/krimi/tatort/vorschau/index.html";

    public enum ScheduleType {
        ERSTE(1),
        DRITTE(2);

        private int offset;

        ScheduleType(Integer offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset;
        }
    }

    public List<TatortBroadcast> ParseTatortWebsite() {

        Document page;
        try {
            page = Jsoup.connect(TATORT_URL).get();
        } catch (IOException e) {
            return new LinkedList<TatortBroadcast>();
        }
        var broadcasts = parseHtml(page.html());
        return broadcasts;
    }


    public static List<TatortBroadcast> parseHtml(String html, ScheduleType schedule) {
        var soup = Jsoup.parse(html, Parser.htmlParser());

        ZonedDateTime requestTimestamp;
        var commentLine = html.substring(html.length() - 100, html.length());
        final Pattern p = Pattern.compile("(<!-- .* @ .* -->)", Pattern.DOTALL);
        final Matcher m = p.matcher(commentLine);
        final boolean found = m.find();

        if (!found) {
            requestTimestamp = ZonedDateTime.now();
        } else {
            requestTimestamp = parseRequestTime(m.group(1));
        }

        // Found linklists:
        // 0:"nächste Erstausstrahlung"
        // 1:"im Ersten"
        // 2:"in den Dritten"
        // 3:"auf ONE"
        // 4:"Tatort in Ihrem dritten Programm"
        var linklist = soup.body().getElementsByClass("linklist");
        // sometimes there are more than 5 entries, e.g. when there are special episodes
        int indexOffset = (int) linklist.size() - 5;
        var tatortList = linklist.get(schedule.getOffset() + indexOffset).getElementsByTag("a");

        List<TatortBroadcast> broadcastList = new LinkedList<TatortBroadcast>();

        for (var linkElement : tatortList) {
            var broadcast = parseRow(linkElement.text(), requestTimestamp, schedule);
            broadcast.url = linkElement.attr("href");
            if (!broadcast.url.startsWith("https://www.daserste.de")) {
                broadcast.url = "https://www.daserste.de" + broadcast.url;
            }
            broadcastList.add(broadcast);
        }

        return broadcastList;
    }

    public static List<TatortBroadcast> parseHtml(String html) {
        return parseHtml(html, ScheduleType.ERSTE);
    }

    public static ZonedDateTime parseRequestTime(String comment) {
        final Pattern p = Pattern.compile("@ (.*) -->", Pattern.DOTALL);
        final Matcher m = p.matcher(comment.trim());
        final Boolean found = m.find();
        String datetime = m.group(1).substring(4);

        datetime = datetime.replace("CEST", "CET");

        DateTimeFormatter df = new DateTimeFormatterBuilder()
                // case insensitive to parse JAN and FEB
                .parseCaseInsensitive()
                // add pattern
                .appendPattern("MMM dd HH:mm:ss v yyyy")
                // create formatter (use English Locale to parse month names)
                .toFormatter(Locale.ENGLISH);

        return ZonedDateTime.parse(datetime, df);
    }

    public static TatortBroadcast parseRow(String row, ZonedDateTime requestTimestamp, ScheduleType schedule) {
        TatortBroadcast tb;
        var columns = row.split(" \\| ");

        if (schedule == ScheduleType.ERSTE) {
            tb = parseTitle(columns[2]);
        } else if (schedule == ScheduleType.DRITTE) {
            tb = parseTitle(columns[3]);
            tb.channel = columns[2];
        } else {
            throw new IllegalArgumentException();
        }
        var time = parseDatetime(columns[0], columns[1], requestTimestamp);
        tb.time = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        return tb;
    }

    public static ZonedDateTime parseDatetime(String dateText, String timeText, ZonedDateTime requestTimestamp) {

        int year, month, day, hour, minute;

        if (dateText.contains("Heute")) {
            year = requestTimestamp.getYear();
            month = requestTimestamp.getMonthValue();
            day = requestTimestamp.getDayOfMonth();
        } else if (dateText.contains("Morgen")) {
            ZonedDateTime tomorrow = requestTimestamp.plusDays(1);
            year = tomorrow.getYear();
            month = tomorrow.getMonthValue();
            day = tomorrow.getDayOfMonth();
        } else {
            var splitText = dateText.split(", ");
            var dateParts = splitText[1].split("\\.");
            year = requestTimestamp.getYear();
            month = Integer.parseInt(dateParts[1]);
            day = Integer.parseInt(dateParts[0]);
        }

        hour = Integer.parseInt(timeText.substring(0, 2));
        minute = Integer.parseInt(timeText.substring(3, 5));

        // if month of date is smaller than month of request, the broadcast is in the next year
        if (month < requestTimestamp.getMonthValue()) {
            year += 1;
        }
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.of("Europe/Berlin"));
    }

    // returns a
    public static TatortBroadcast parseTitle(String titleText) {
        TatortBroadcast partial = new TatortBroadcast();
        // Parse City

        int cityBegin = titleText.lastIndexOf('(') + 1;
        int cityEnd = titleText.indexOf(')', cityBegin);
        partial.city = titleText.substring(cityBegin, cityEnd).trim();

        // Parse Inspectors
        String titleNoCity = titleText.substring(0, cityBegin - 1);
        int inspectorBegin = titleNoCity.lastIndexOf('(') + 1;
        partial.inspectors = titleNoCity.substring(inspectorBegin).trim();

        // Parse Title
        partial.title = titleText.substring(0, inspectorBegin - 2).trim();
        return partial;
    }


}
