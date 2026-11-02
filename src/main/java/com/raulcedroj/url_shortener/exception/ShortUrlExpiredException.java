package com.raulcedroj.url_shortener.exception;

public class ShortUrlExpiredException extends RuntimeException {

    public ShortUrlExpiredException(String code) {
        super("La URL con el código " + code + " ha caducado");
    }
}