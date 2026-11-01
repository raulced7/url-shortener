package com.raulcedroj.url_shortener.service;

import com.raulcedroj.url_shortener.repository.ShortUrlRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClickTracker {

    private final ShortUrlRepository repository;

    public ClickTracker(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Async
    @Transactional
    public void recordClick(String code) {
        repository.incrementClicks(code);
    }
}
