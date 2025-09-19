package com.simple.Bookstore.views.Review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReviewViewError {
    INVALID_ASSOCIATION("Invalid Association");
    private final String displayName;
}
