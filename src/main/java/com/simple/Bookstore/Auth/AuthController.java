package com.simple.Bookstore.Auth;

import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.User.User;
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
        // Basic validation (you'd want more robust validation)
        if (userService.findByUsername(request.username()).isPresent()) {
            return new ResponseEntity<>("Username already taken!", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(request.password()); // Password will be encoded by userService
        newUser.setRole(Role.USER); // Default role for new registrations
        newUser.setDisplayName(request.displayName() != null ? request.displayName() : request.username());

        userService.createUser(newUser);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }
}
