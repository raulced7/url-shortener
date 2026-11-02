package com.raulcedroj.url_shortener.ratelimit;

import com.raulcedroj.url_shortener.config.RateLimitProperties;
import com.raulcedroj.url_shortener.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redis;
    private final RateLimitProperties properties;

    public RateLimitInterceptor(StringRedisTemplate redis, RateLimitProperties properties) {
        this.redis = redis;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }

        String key = "rate-limit:" + request.getRemoteAddr();
        Long count = redis.opsForValue().increment(key);

        if (count != null && count == 1) {
            redis.expire(key, WINDOW);
        }

        int limit = properties.requestsPerMinute();
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limit - count)));

        if (count > limit) {
            Long ttl = redis.getExpire(key, TimeUnit.SECONDS);
            throw new RateLimitExceededException(ttl != null && ttl > 0 ? ttl : WINDOW.toSeconds());
        }
        return true;
    }
}