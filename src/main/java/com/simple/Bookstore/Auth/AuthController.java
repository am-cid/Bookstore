package com.simple.Bookstore.Auth;

import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.User.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            userService.createUser(request, Role.USER);
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (
                UsernameAlreadyTakenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (
                Exception e) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
