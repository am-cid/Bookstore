package com.simple.Bookstore.views.Review;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.Result;
import org.springframework.data.domain.Pageable;

public interface ReviewViewService {
    /**
     * validates whether user can access a review thread page or not.
     *
     * @param user     current user
     * @param bookId   book id in path
     * @param reviewId review id in path
     * @param pageable comment pages
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<ReviewViewModel, String> validatePageAccess(
            User user,
            Long bookId,
            Long reviewId,
            Pageable pageable
    ) throws BookNotFoundException, ReviewNotFoundException;

    /**
     * validates whether user can create a comment on a review thread page or not.
     *
     * @param user     current user
     * @param bookId   book id in path
     * @param reviewId review id in path
     * @param pageable comment pages
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<ReviewViewModel, String> validateCommentCreationAccess(
            User user,
            Long bookId,
            Long reviewId,
            Pageable pageable
    ) throws BookNotFoundException, ReviewNotFoundException, UnauthorizedException;
}
