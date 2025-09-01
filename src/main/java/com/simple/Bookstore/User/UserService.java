package com.simple.Bookstore.User;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Role.Role;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    User createUser(RegisterRequestDTO request, Role role) throws UsernameAlreadyTakenException;

    /**
     * Updates User entity as well as the SecurityContextHolder which caches
     * the User entity from the @AuthenticationPrincipal
     *
     * @param user    User entity
     * @param request user update request
     * @return updated user
     * @throws UsernameAlreadyTakenException when user update request changes username to an already taken username
     */
    User updateUser(User user, UserUpdateRequestDTO request) throws UsernameAlreadyTakenException;

    /**
     * use when the password in the request is optional and cannot be validated by @Valid.
     * <p>
     * Password must be between 8  and 30 characters
     *
     * @param password to be validated
     * @return optional error message
     */
    Optional<String> isValidPasswordLength(String password);

    /**
     * use when the password in the request is optional and cannot be validated by @Valid.
     * <p>
     * Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character.
     *
     * @param password to be validated
     * @return optional error message
     */
    Optional<String> isValidPasswordPattern(String password);
}
