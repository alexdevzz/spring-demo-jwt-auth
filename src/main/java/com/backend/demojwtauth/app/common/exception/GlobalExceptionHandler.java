package com.backend.demojwtauth.app.common.exception;

import com.backend.demojwtauth.app.common.dto.ApiResponse;
import com.backend.demojwtauth.app.common.dto.ErrorDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Global Exception Handler for standardize error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;

    /**
     * Validation Exceptions Handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

        List<ErrorDetails.ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();

                    return ErrorDetails.ValidationError.builder()
                            .field(fieldName)
                            .message(errorMessage)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .toList();

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("VALIDATION_ERROR")
                .errorType("VALIDATION")
                .validationErrors(validationErrors)
                .build();

        ApiResponse<Void> response = ApiResponse.error(
                "Validation failed for one or more fields",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Bad Credential Handler
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("BAD_CREDENTIALS")
                .errorType("AUTHENTICATION")
                .build();

        ApiResponse<Void> response = ApiResponse.error(
                "Invalid username or password",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * User Not Found Handler
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("USER_NOT_FOUND")
                .errorType("AUTHENTICATION")
                .build();

        ApiResponse<Void> response = ApiResponse.error(
                "User not found",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * JWT expired token handler
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("TOKEN_EXPIRED")
                .errorType("AUTHENTICATION")
                .build();

        ApiResponse<Void> response = ApiResponse.error(
                "JWT token has expired",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles JWT errors in general
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("INVALID_TOKEN")
                .errorType("AUTHENTICATION")
                .build();

        ApiResponse<Void> response = ApiResponse.error(
                "Invalid or malformed JWT token",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * General authentication errors handler
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("AUTHENTICATION_FAILED")
                .errorType("AUTHENTICATION")
                .build();

        if (debugMode) {
            errorDetails.setStackTrace(getStackTraceAsString(ex));
        }

        ApiResponse<Void> response = ApiResponse.error(
                "Authentication failed: " + ex.getMessage(),
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Custom business exception handler
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .errorType("BUSINESS")
                .build();

        if (debugMode) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("details", ex.getDetails());
            errorDetails.setDebugInfo(debugInfo);
        }

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                errorDetails
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * Handle any other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorType("SYSTEM")
                .build();

        if (debugMode) {
            errorDetails.setStackTrace(getStackTraceAsString(ex));
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("exceptionType", ex.getClass().getName());
            debugInfo.put("message", ex.getMessage());
            errorDetails.setDebugInfo(debugInfo);
        }

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    /**
     * Transform the stack trace to string
     * @param ex General Exception
     * @return stack trace to string
     */
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append(ste.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
