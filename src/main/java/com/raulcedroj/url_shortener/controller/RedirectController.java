package com.raulcedroj.url_shortener.controller;

import com.raulcedroj.url_shortener.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Redirect", description = "Redirects to the original URL and counts the click")
@RestController
public class RedirectController {

    private final ShortUrlService service;

    public RedirectController(ShortUrlService service) {
        this.service = service;
    }

    @Operation(summary = "Redirect to the original URL",
            description = "Returns 302 Found with the original URL in the Location header. Test it in the browser.")
    @ApiResponse(responseCode = "302", description = "Redirect to the original URL")
    @ApiResponse(responseCode = "404", description = "Code not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{code:[0-9a-zA-Z]{7}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(service.getRedirectUrl(code)))
                .build();
    }
}
