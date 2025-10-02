package com.simple.Bookstore.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String CUSTOM_ERROR_TITLE_ATTRIBUTE = "custom.error.title";
    public static final String CUSTOM_ERROR_MESSAGE_ATTRIBUTE = "custom.error.message";

    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Book Not Found");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "forward:/error";
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public String handleCommentNotFoundException(
            BookNotFoundException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Comment Not Found");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "forward:/error";
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public String handleReviewNotFoundException(
            BookNotFoundException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Review Not Found");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "forward:/error";
    }

    @ExceptionHandler(ThemeNotFoundException.class)
    public String handleThemeNotFoundException(
            ThemeNotFoundException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Theme Not Found");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "forward:/error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Unauthorized");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return "forward:/error";
    }

    @ExceptionHandler(UnreachableException.class)
    public String handleUnreachableException(
            UnreachableException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Unreachable");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "forward:/error";
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public String handleUsernameAlreadyTakenException(
            UsernameAlreadyTakenException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "Username Already Taken");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.CONFLICT.value());
        return "forward:/error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        request.setAttribute(CUSTOM_ERROR_TITLE_ATTRIBUTE, "User Not Found");
        request.setAttribute(CUSTOM_ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "forward:/error";
    }
}