package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProfileViewSavedBooks(
        Page<BookSearchResultDTO> savedBooks,
        List<Long> currentUserSavedBookIds
) {
}
