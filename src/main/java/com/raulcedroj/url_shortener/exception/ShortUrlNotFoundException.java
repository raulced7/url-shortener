package com.raulcedroj.url_shortener.exception;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String code) {
        super("No existe ninguna URL con el código: " + code);
    }
}
