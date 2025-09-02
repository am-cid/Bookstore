package com.simple.Bookstore.utils;

import java.util.function.Function;

public sealed interface Result<T, E> {
    default boolean isOk() {
        return this instanceof Result.Ok;
    }

    default boolean isErr() {
        return this instanceof Result.Err;
    }

    default T unwrap() throws RuntimeException {
        if (this instanceof Ok<T, E>(
                T result
        )) {
            return result;
        }
        throw new RuntimeException("Called unwrap() on a Err result");
    }

    default E unwrapErr() throws RuntimeException {
        if (this instanceof Err<T, E>(
                E error
        )) {
            return error;
        }
        throw new RuntimeException("Called unwrapErr() on an Ok result");
    }

    default T unwrapOrElse(Function<E, T> orElse) throws IllegalStateException {
        if (this instanceof Ok<T, E>(
                T result
        )) {
            return result;
        }
        if (this instanceof Err<T, E>(
                E error
        )) {
            return orElse.apply(error);
        }
        throw new IllegalStateException("Unknown Result type");
    }

    default T unwrapOrElseThrow(Function<E, ? extends RuntimeException> orElseThrow) throws RuntimeException {
        if (this instanceof Ok<T, E>(
                T result
        )) {
            return result;
        }
        if (this instanceof Err<T, E>(
                E error
        )) {
            throw orElseThrow.apply(error);
        }
        throw new IllegalStateException("Unknown Result type");
    }

    record Ok<T, E>(
            T result
    ) implements Result<T, E> {
    }

    record Err<T, E>(
            E error
    ) implements Result<T, E> {
    }
}
