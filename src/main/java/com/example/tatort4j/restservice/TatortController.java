package com.example.tatort4j.restservice;

import com.example.tatort4j.scraper.Scraper;
import com.example.tatort4j.scraper.TatortBroadcast;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TatortController {
    @GetMapping("/schedule")
    List<TatortBroadcast> getSchedule() {
        Scraper s = new Scraper();
        System.out.println("get schedule");
        return s.ParseTatortWebsite();
    }
}
