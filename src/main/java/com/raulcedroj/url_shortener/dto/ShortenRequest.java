package com.raulcedroj.url_shortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record ShortenRequest(
        @Schema(example = "https://www.github.com")
        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "^https?://\\S+$", message = "debe ser una URL http o https válida")
        String url,

        @Schema(example = "2026-12-31T23:59:59Z", description = "Optional expiration date (ISO-8601, UTC)")
        @Future
        Instant expiresAt
) {
}
