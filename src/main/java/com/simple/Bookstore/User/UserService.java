package com.simple.Bookstore.User;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Role.Role;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    void createUser(RegisterRequestDTO request, Role role) throws UsernameAlreadyTakenException;
}
