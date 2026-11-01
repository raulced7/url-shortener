package com.raulcedroj.url_shortener.service;

import com.raulcedroj.url_shortener.entity.ShortUrl;
import com.raulcedroj.url_shortener.exception.ShortUrlNotFoundException;
import com.raulcedroj.url_shortener.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShortUrlService {

    private static final int MAX_ATTEMPTS = 5;

    private final ShortUrlRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final ClickTracker clickTracker;

    public ShortUrlService(ShortUrlRepository repository,
                           ShortCodeGenerator codeGenerator,
                           ClickTracker clickTracker) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.clickTracker = clickTracker;
    }

    @Transactional
    public ShortUrl shorten(String originalUrl) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = codeGenerator.generate();
            if (!repository.existsByCode(code)) {
                return repository.save(new ShortUrl(code, originalUrl));
            }
        }
        throw new IllegalStateException("No se ha podido generar un código único");
    }

    @Transactional(readOnly = true)
    public ShortUrl resolve(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
    }

    public String getRedirectUrl(String code) {
        String originalUrl = repository.findByCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code))
                .getOriginalUrl();
        clickTracker.recordClick(code);
        return originalUrl;
    }
}