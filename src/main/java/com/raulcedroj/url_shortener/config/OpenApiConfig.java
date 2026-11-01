package com.raulcedroj.url_shortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urlShortenerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("URL Shortener API")
                .version("1.0.0")
                .description("REST API to shorten URLs, redirect to the original URL and track clicks.")
                .contact(new Contact()
                        .name("Raúl Cedeño Rojo")
                        .url("https://www.linkedin.com/in/raulcedroj/")));
    }
}