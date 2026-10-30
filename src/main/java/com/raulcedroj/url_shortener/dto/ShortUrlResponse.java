package com.raulcedroj.url_shortener.dto;

import com.raulcedroj.url_shortener.entity.ShortUrl;

import java.time.Instant;

public record ShortUrlResponse(
        String code,
        String shortUrl,
        String originalUrl,
        Instant createdAt
) {
    public static ShortUrlResponse from(ShortUrl entity, String shortUrl) {
        return new ShortUrlResponse(entity.getCode(), shortUrl,
                entity.getOriginalUrl(), entity.getCreatedAt());
    }
}
