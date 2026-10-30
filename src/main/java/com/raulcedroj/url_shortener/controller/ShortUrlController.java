package com.raulcedroj.url_shortener.controller;

import com.raulcedroj.url_shortener.dto.ShortUrlResponse;
import com.raulcedroj.url_shortener.dto.ShortenRequest;
import com.raulcedroj.url_shortener.entity.ShortUrl;
import com.raulcedroj.url_shortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(shortUrl.getCode())
                .toUri();

        return ResponseEntity.created(location)
                .body(ShortUrlResponse.from(shortUrl, location.toString()));
    }
}
