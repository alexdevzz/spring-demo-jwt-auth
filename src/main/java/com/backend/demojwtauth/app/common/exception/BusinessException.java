package com.backend.demojwtauth.app.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom exception for business logic errors
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;
    private final Map<String, Object> details;

    public BusinessException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = new HashMap<>();
    }

    public BusinessException(String message, String errorCode, HttpStatus status, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details != null ? details : new HashMap<>();
    }

    public BusinessException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    /**
     * factory methods for common uses
     */
    public static BusinessException userAlreadyExists(String username) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        return new BusinessException(
                "User with username '" + username + "' already exists",
                "USER_ALREADY_EXISTS",
                HttpStatus.CONFLICT,
                details
        );
    }

    public static BusinessException userNotFound(String username) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        return new BusinessException(
                "User with username '" + username + "' not found",
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                details
        );
    }

    public static BusinessException resourceNotFound(String resource, String identifier) {
        Map<String, Object> details = new HashMap<>();
        details.put("resource", resource);
        details.put("identifier", identifier);
        return new BusinessException(
                resource + " not found with identifier: " + identifier,
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                details
        );
    }

    public static BusinessException invalidOperation(String operation, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("operation", operation);
        details.put("reason", reason);
        return new BusinessException(
                "Invalid operation: " + operation + ". Reason: " + reason,
                "INVALID_OPERATION",
                HttpStatus.BAD_REQUEST,
                details
        );
    }
}
