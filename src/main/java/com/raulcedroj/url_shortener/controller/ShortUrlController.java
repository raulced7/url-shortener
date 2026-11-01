package com.raulcedroj.url_shortener.controller;

import com.raulcedroj.url_shortener.dto.ShortUrlResponse;
import com.raulcedroj.url_shortener.dto.ShortenRequest;
import com.raulcedroj.url_shortener.entity.ShortUrl;
import com.raulcedroj.url_shortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortUrl shortUrl = service.shorten(request.url());
        String link = buildShortUrl(shortUrl.getCode());

        return ResponseEntity.created(URI.create(link))
                .body(ShortUrlResponse.from(shortUrl, link));
    }

    @GetMapping("/{code}")
    public ShortUrlResponse getStats(@PathVariable String code) {
        ShortUrl shortUrl = service.resolve(code);
        return ShortUrlResponse.from(shortUrl, buildShortUrl(code));
    }

    private String buildShortUrl(String code) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(code)
                .toUriString();
    }
}