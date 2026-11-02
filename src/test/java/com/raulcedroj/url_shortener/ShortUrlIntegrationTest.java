package com.raulcedroj.url_shortener;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ShortUrlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shortensUrlAndRedirectsToOriginal() throws Exception {
        String response = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "url": "https://www.google.com" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String code = JsonPath.read(response, "$.code");

        mockMvc.perform(get("/{code}", code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.google.com"));
    }

    @Test
    void rejectsInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "url": "hola" }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownCode() throws Exception {
        mockMvc.perform(get("/abcdefg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Short URL not found"));
    }

    @Test
    void countsClicksOnEachRedirect() throws Exception {
        String response = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "url": "https://www.github.com" }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String code = JsonPath.read(response, "$.code");

        mockMvc.perform(get("/{code}", code)).andExpect(status().isFound());
        mockMvc.perform(get("/{code}", code)).andExpect(status().isFound());

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                mockMvc.perform(get("/api/urls/{code}", code))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.clicks").value(2)));
    }

    @Test
    void exposesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("URL Shortener API"));
    }

    @Test
    void cachesOriginalUrlAfterFirstRedirect() throws Exception {
        String response = mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "url": "https://spring.io" }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String code = JsonPath.read(response, "$.code");
        String cacheKey = "original-urls::" + code;

        assertThat(redisTemplate.hasKey(cacheKey)).isFalse();

        mockMvc.perform(get("/{code}", code)).andExpect(status().isFound());

        assertThat(redisTemplate.opsForValue().get(cacheKey)).isEqualTo("https://spring.io");
    }
}