package com.example.tatort4j.providers;

import com.example.tatort4j.scraper.TatortBroadcast;
import java.util.List;

public interface ITatortProvider {
    List<TatortBroadcast> getTatortSchedule();
}
