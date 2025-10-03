package com.simple.Bookstore.Auth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException )
            throws IOException, ServletException {
        String redirectUrl = "/register?error=need_auth";
        redirectStrategy.sendRedirect( request,response,redirectUrl );
    }

}
