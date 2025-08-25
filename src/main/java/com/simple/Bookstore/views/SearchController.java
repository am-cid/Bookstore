package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Search.SearchType;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final BookService bookService;
    private final ThemeService themeService;
    private final ProfileService profileService;
    private final ReviewService reviewService;

    @GetMapping("/search")
    public String search(
            @RequestParam("type") SearchType searchType,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Set<Genre> genres,
            @RequestParam(required = false) Double rating,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        // search result data
        model.addAttribute("searchType", searchType);
        model.addAttribute("queryParam", query);
        model.addAttribute("genresParam", genres);
        model.addAttribute("ratingParam", rating);
        Page<?> results = switch (searchType) {
            case BOOK ->
                    bookService.searchBooks(query, genres, rating, pageable);
            case THEME ->
                    themeService.searchThemes(
                            query,
                            (user != null)
                                    ? user.getId()
                                    : null,
                            pageable
                    );
            case PROFILE ->
                    profileService.searchProfiles(query, user, pageable);
        };
        model.addAttribute("results", results);
        return "search";
    }
}
