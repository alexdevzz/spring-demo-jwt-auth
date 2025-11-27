package com.backend.demojwtauth.app.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * A filter that catches exceptions thrown by other filters
 * and delegates them to the GlobalExceptionHandler
 */
@Component
public class FilterExceptionHandler extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;

    public FilterExceptionHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // delegate the exception to GlobalExceptionHandler
            resolver.resolveException(request, response, null, ex);
        }

    }
}
