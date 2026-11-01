package com.raulcedroj.url_shortener.repository;

import com.raulcedroj.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByCode(String code);

    boolean existsByCode(String code);

    @Modifying
    @Query("UPDATE ShortUrl s SET s.clicks = s.clicks + 1 WHERE s.code = :code")
    int incrementClicks(@Param("code") String code);
}