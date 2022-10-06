package com.example.tatort4j.providers;

import com.example.tatort4j.scraper.Scraper;
import com.example.tatort4j.scraper.TatortBroadcast;
import com.example.tatort4j.scraper.Scraper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("basicTatortProvider")
public class BasicTatortProvider implements ITatortProvider {
    private final Scraper scraper = new Scraper();

    public List<TatortBroadcast> getTatortSchedule() {
        System.out.println("basictatortprovider");
        return scraper.ParseTatortWebsite();
    }
}
