package com.example.tatort4j.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    final String TATORT_URL = "https://www.daserste.de/unterhaltung/krimi/tatort/vorschau/index.html";

    public List<TatortBroadcast> ParseTatortWebsite() {
        List<TatortBroadcast> broadcasts = new ArrayList<TatortBroadcast>();

        Document page;
        try {
            page = Jsoup.connect(TATORT_URL).get();
        }
        catch (IOException e) {
            return broadcasts;
        }

        return broadcasts;
    }

    private TatortBroadcast parseRow(Document d) {
        TatortBroadcast tb = new TatortBroadcast();

        return tb;
    }
    private LocalDateTime parseDatetime(String dateText, String timeText, String requestTimestamp) {
        LocalDateTime t = new LocalDateTime();

        return t;
    }

    // returns a
    public TatortBroadcast parseTitle(String titleText) {
        TatortBroadcast partial = new TatortBroadcast();
        // Parse City

        String[] split = titleText.split("\\(");
        int cityBegin = titleText.lastIndexOf('(')+1;
        int cityEnd = titleText.indexOf(')', cityBegin);
        partial.city = titleText.substring(cityBegin, cityEnd);

        // Parse Inspectors
        int inspectorBegin = titleText.lastIndexOf('(', );
        partial.inspectors = "";

        // Parse Title
        partial.title = titleText.substring(0, inspectorBegin-2);
        return partial;
    }


}
