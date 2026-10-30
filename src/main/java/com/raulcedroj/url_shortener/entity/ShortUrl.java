package com.raulcedroj.url_shortener.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "short_url")
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false)
    private long clicks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ShortUrl() {
    }

    public ShortUrl(String code, String originalUrl) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getOriginalUrl() { return originalUrl; }
    public long getClicks() { return clicks; }
    public Instant getCreatedAt() { return createdAt; }
}