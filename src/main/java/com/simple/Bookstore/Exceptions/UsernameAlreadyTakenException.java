package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException() {
        super("Username already taken!");
    }

    public UsernameAlreadyTakenException(String username) {
        super("Username \"" + username + "\" is already taken! ");
    }
}
