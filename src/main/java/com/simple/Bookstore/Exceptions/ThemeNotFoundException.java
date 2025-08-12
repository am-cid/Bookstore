package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException() {
        super("Theme not found or not published!");
    }

    public ThemeNotFoundException(String name) {
        super("Theme \"" + name + "\" not found or not published! ");
    }

    public ThemeNotFoundException(Long id) {
        super("Theme with ID " + id + " not found or not published!");
    }
}
