package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * when user cannot perform said action
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You are not allowed to perform this action.");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
