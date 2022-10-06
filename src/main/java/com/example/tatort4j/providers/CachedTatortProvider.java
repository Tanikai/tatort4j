package com.example.tatort4j.providers;

import com.example.tatort4j.scraper.Scraper;
import com.example.tatort4j.scraper.TatortBroadcast;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component("cachedTatortProvider")
public class CachedTatortProvider implements ITatortProvider {

    private final Scraper scraper = new Scraper();
    private List<TatortBroadcast> cachedSchedule;
    private LocalDateTime lastUpdate = LocalDateTime.MIN;

    public List<TatortBroadcast> getTatortSchedule() {
        LocalDateTime now = LocalDateTime.now();
        int cacheDuration = 1;
        if ( Duration.between(lastUpdate, now).toMinutes() >= cacheDuration) {
            cachedSchedule = scraper.ParseTatortWebsite();
            lastUpdate = now;
            System.out.println(now + " Refreshing Schedule");
        }
        return cachedSchedule;
    }
}
