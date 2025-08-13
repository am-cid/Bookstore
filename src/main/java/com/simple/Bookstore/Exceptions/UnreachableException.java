package com.simple.Bookstore.Exceptions;

/**
 * Use this to denote that the code path is unreachable
 */
public class UnreachableException extends RuntimeException {
    public UnreachableException() {
        super("UNREACHABLE");
    }

    public UnreachableException(String message) {
        super("UNREACHABLE: " + message);
    }
}
