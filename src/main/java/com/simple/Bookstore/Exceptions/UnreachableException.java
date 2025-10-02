package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Use this to denote that the code path is unreachable
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnreachableException extends RuntimeException {
    public UnreachableException() {
        super("UNREACHABLE");
    }

    public UnreachableException(String message) {
        super("UNREACHABLE: " + message);
    }
}
