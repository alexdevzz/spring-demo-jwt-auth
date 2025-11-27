package com.backend.demojwtauth.app.config.security;

import com.backend.demojwtauth.app.common.dto.ApiResponse;
import com.backend.demojwtauth.app.common.dto.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles errors when an AUTHENTICATED user does not have permissions (incorrect role)
 * Maneja errores cuando un usuario AUTENTICADO no tiene permisos (rol incorrecto)
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("ACCESS_DENIED")
                .errorType("AUTHORIZATION")
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.error(
                "You don't have permission to access this resource. ADMIN role required.",
                errorDetails
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
