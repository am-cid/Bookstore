package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;

import java.util.Set;

public record ProfileResponseDTO(
        String displayName,
        Set<ThemeResponseDTO> ownedThemes,
        Set<ThemeResponseDTO> savedThemes,
        Set<BookSearchResultDTO> savedBooks
) {
}
