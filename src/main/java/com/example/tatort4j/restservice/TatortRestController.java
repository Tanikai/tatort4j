package com.example.tatort4j.restservice;

import com.example.tatort4j.providers.ITatortProvider;
import com.example.tatort4j.scraper.TatortBroadcast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tatort")
public class TatortRestController {

    private final ITatortProvider tatortProvider;

    @Autowired
    public TatortRestController(@Qualifier("cachedTatortProvider") ITatortProvider tProvider) {
        this.tatortProvider = tProvider;
    }

    @GetMapping("/schedule")
    List<TatortBroadcast> getSchedule() {
        List<TatortBroadcast> schedule = tatortProvider.getTatortSchedule();
        return schedule;
    }
}
