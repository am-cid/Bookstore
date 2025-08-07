package com.simple.Bookstore.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> findByUsername(String username);
}
