package com.raulcedroj.url_shortener.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void generatesSevenCharacterBase62Code() {
        String code = generator.generate();

        assertThat(code).hasSize(7).matches("[0-9a-zA-Z]+");
    }
}
