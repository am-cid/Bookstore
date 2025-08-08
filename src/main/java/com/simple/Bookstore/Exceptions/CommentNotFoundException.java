package com.simple.Bookstore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found! ");
    }

    public CommentNotFoundException(String message) {
        super("Comment not found! " + message);
    }

    public CommentNotFoundException(Long id) {
        super("Comment with ID " + id + " not found");
    }
}
