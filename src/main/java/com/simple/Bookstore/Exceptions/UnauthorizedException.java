package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * throw when user is not authenticated
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("You need to login to perform this action");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
