package com.simple.Bookstore.views.SharedModels;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record ViewBooksModel(
        Page<BookSearchResultDTO> books,
        List<Long> currentUserSavedBookIds
) {
}
