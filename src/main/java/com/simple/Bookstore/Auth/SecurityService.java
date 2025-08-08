package com.simple.Bookstore.Auth;

import com.simple.Bookstore.User.User;

public interface SecurityService {
    public User getLoggedInUser();
}
