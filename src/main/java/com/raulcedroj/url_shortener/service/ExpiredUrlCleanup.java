package com.raulcedroj.url_shortener.service;

import com.raulcedroj.url_shortener.config.CacheConfig;
import com.raulcedroj.url_shortener.entity.ShortUrl;
import com.raulcedroj.url_shortener.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Component
public class ExpiredUrlCleanup {

    private static final Logger log = LoggerFactory.getLogger(ExpiredUrlCleanup.class);

    private final ShortUrlRepository repository;
    private final CacheManager cacheManager;

    public ExpiredUrlCleanup(ShortUrlRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedDelayString = "${app.cleanup.fixed-delay-ms:60000}")
    @Transactional
    public void removeExpiredUrls() {
        List<ShortUrl> expired = repository.findByExpiresAtBefore(Instant.now());
        if (expired.isEmpty()) {
            return;
        }

        repository.deleteAllInBatch(expired);

        Cache cache = cacheManager.getCache(CacheConfig.ORIGINAL_URLS);
        if (cache != null) {
            expired.forEach(url -> cache.evict(url.getCode()));
        }

        log.info("Removed {} expired short URLs", expired.size());
    }
}