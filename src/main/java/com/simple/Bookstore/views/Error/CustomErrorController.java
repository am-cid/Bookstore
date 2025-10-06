package com.simple.Bookstore.views.Error;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Exceptions.GlobalExceptionHandler;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.presentation.Kaomoji.ErrorMascotFactory;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";
    private final BookService bookService;
    private final ThemeService themeService;
    private final ReviewService reviewService;

    @RequestMapping(ERROR_PATH)
    public String handleError(
            @AuthenticationPrincipal User user,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        model.addAttribute("httpStatus", httpStatus.value());
        model.addAttribute("path", request.getRequestURI());

        Object customTitleAttr = request.getAttribute(GlobalExceptionHandler.CUSTOM_ERROR_TITLE_ATTRIBUTE);
        Object customMessageAttr = request.getAttribute(GlobalExceptionHandler.CUSTOM_ERROR_MESSAGE_ATTRIBUTE);
        model.addAttribute("title",
                customTitleAttr != null
                        ? customTitleAttr.toString()
                        : httpStatus.getReasonPhrase()
        );
        model.addAttribute("context",
                customMessageAttr != null
                        ? customMessageAttr.toString()
                        : (switch (httpStatus) {
                    case OK -> // 200 (when /error is manually visited)
                            "Well, that's embarrassing. You aren't supposed to visit this page manually!!";
                    case UNAUTHORIZED -> // 401
                            "You need to login to perform this action";
                    case FORBIDDEN -> // 401
                            "You are not allowed to perform this action";
                    case NOT_FOUND -> // 404
                            "Page not found. Check if the URL is correct.";
                    default ->
                            "An unexpected error occurred.";
                })
        );
        model.addAttribute("errorMascot", ErrorMascotFactory.random(httpStatus));
        return "error";
    }
}
