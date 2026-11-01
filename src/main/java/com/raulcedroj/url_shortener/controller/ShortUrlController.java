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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;

@Tag(name = "Short URLs", description = "Create short URLs and check their statistics")
@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @Operation(summary = "Create a short URL")
    @ApiResponse(responseCode = "201", description = "Short URL created")
    @ApiResponse(responseCode = "400", description = "Invalid URL",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<ShortUrlResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortUrl shortUrl = service.shorten(request.url());
        String link = buildShortUrl(shortUrl.getCode());

        return ResponseEntity.created(URI.create(link))
                .body(ShortUrlResponse.from(shortUrl, link));
    }

    @Operation(summary = "Get the statistics of a short URL")
    @ApiResponse(responseCode = "200", description = "Statistics found")
    @ApiResponse(responseCode = "404", description = "Code not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
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