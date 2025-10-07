package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Search.SearchType;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.views.SharedModels.ViewBooksModel;
import com.simple.Bookstore.views.SharedModels.ViewProfilesModel;
import com.simple.Bookstore.views.SharedModels.ViewThemesAsPageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SearchViewController {
    private final BookService bookService;
    private final ThemeService themeService;
    private final ProfileService profileService;
    private final ReviewService reviewService;

    // HELPERS
    private static String queryString(
            SearchType searchType,
            Optional<String> query,
            Optional<Double> rating,
            Optional<Set<Genre>> genres
    ) {
        StringBuilder res = new StringBuilder();
        res.append("type=");
        res.append(searchType.name());
        res.append("&query=");
        query.ifPresent(res::append);
        res.append("&rating=");
        rating.ifPresent(res::append);
        genres.ifPresent((g) -> {
            for (Genre genre : g) {
                res.append("&genres=");
                res.append(genre.name());
            }
        });
        return res.toString();
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("type") SearchType searchType,
            @RequestParam(required = false) Optional<String> query,
            // need specifically for collection types since a null collection's
            // type cannot be inferred by JPA in repository native queries,
            // even if there is a null check in the WHERE clause:
            // ':genres IS NULL OR'
            @RequestParam(required = false) Optional<Set<Genre>> genres,
            @RequestParam(required = false) Optional<Double> rating,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        // search result data
        switch (searchType) {
            case BOOK -> {
                Page<BookSearchResultDTO> results = bookService.searchBooks(query, genres, rating, pageable);
                model.addAttribute("results", results);
                model.addAttribute(
                        "viewBooksModel",
                        new ViewBooksModel(
                                results,
                                bookService.findSavedBookIds(user)
                        )
                );
            }
            case THEME -> {
                Page<ThemeResponseDTO> results = themeService.searchThemes(query, user, pageable);
                model.addAttribute("results", results);
                model.addAttribute(
                        "viewThemesAsPageModel",
                        new ViewThemesAsPageModel(
                                results,
                                themeService.findUsedTheme(user),
                                themeService.findSavedThemeIds(user)
                        )
                );
            }
            case PROFILE -> {
                Page<ProfileResponseDTO> results = profileService.searchProfiles(query, user, pageable);
                model.addAttribute("results", results);
                model.addAttribute("viewProfilesModel", new ViewProfilesModel(results));
            }
        }

        model.addAttribute("queryString", queryString(searchType, query, rating, genres));
        return "search";
    }
}
