package com.raulcedroj.url_shortener.service;

import com.raulcedroj.url_shortener.config.CacheConfig;
import com.raulcedroj.url_shortener.entity.ShortUrl;
import com.raulcedroj.url_shortener.exception.ShortUrlNotFoundException;
import com.raulcedroj.url_shortener.repository.ShortUrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class OriginalUrlLookup {

    private final ShortUrlRepository repository;

    public OriginalUrlLookup(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CacheConfig.ORIGINAL_URLS, key = "#code")
    public String findOriginalUrl(String code) {
        return repository.findByCode(code)
                .map(ShortUrl::getOriginalUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
    }
}