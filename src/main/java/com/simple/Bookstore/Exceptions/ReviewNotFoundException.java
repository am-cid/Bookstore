package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("Review not found! ");
    }

    public ReviewNotFoundException(String message) {
        super("Review not found! " + message);
    }

    public ReviewNotFoundException(Long id) {
        super("Review with ID " + id + " not found");
    }


}
