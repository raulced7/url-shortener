package com.raulcedroj.url_shortener.dto;

import com.raulcedroj.url_shortener.entity.ShortUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ShortUrlResponse(
        @Schema(example = "aZ3k9Xy")
        String code,
        String shortUrl,
        String originalUrl,
        long clicks,
        Instant createdAt
) {
    public static ShortUrlResponse from(ShortUrl entity, String shortUrl) {
        return new ShortUrlResponse(entity.getCode(), shortUrl, entity.getOriginalUrl(),
                entity.getClicks(), entity.getCreatedAt());
    }
}