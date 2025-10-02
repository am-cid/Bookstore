package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super("Book not found!");
    }

    public BookNotFoundException(String message) {
        super("Book not found! " + message);
    }

    public BookNotFoundException(Long id) {
        super("Book with ID " + id + " not found.");
    }

}
