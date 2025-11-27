package com.backend.demojwtauth.app.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Class for error details in responses
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

    /**
     * Error code specific for the application
     */
    private String errorCode;

    /**
     * Error type (VALIDATION, AUTHENTICATION, AUTHORIZATION, etc.)
     */
    private String errorType;

    /**
     * List of validation errors (specific field)
     */
    private List<ValidationError> validationErrors;

    /**
     * additional technical information (only in development)
     */
    private Map<String, Object> debugInfo;

    /**
     * Error trace (only in development)
     */
    private String stackTrace;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

}
