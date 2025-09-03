package memoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the MemoApp REST API.
 * 
 * This class follows the Single Responsibility Principle by having a single
 * purpose: handling exceptions and converting them to appropriate HTTP responses.
 * 
 * Uses @ControllerAdvice to provide centralized exception handling across
 * all controllers, following the DRY (Don't Repeat Yourself) principle.
 * 
 * Returns consistent error response format for better API usability.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MemoNotFoundException and returns HTTP 404 Not Found.
     * 
     * @param ex the exception that was thrown
     * @param request the web request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP 404 status
     */
    @ExceptionHandler(MemoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMemoNotFoundException(
            MemoNotFoundException ex, WebRequest request) {
        
        Map<String, Object> body = createErrorBody(
            HttpStatus.NOT_FOUND.value(),
            "Memo Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        // Add memo-specific details if available
        if (ex.getMemoId() != null) {
            body.put("memoId", ex.getMemoId());
        }
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MemoValidationException and returns HTTP 400 Bad Request.
     * 
     * @param ex the exception that was thrown
     * @param request the web request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP 400 status
     */
    @ExceptionHandler(MemoValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMemoValidationException(
            MemoValidationException ex, WebRequest request) {
        
        Map<String, Object> body = createErrorBody(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        // Add validation-specific details if available
        if (ex.getFieldName() != null) {
            body.put("field", ex.getFieldName());
        }
        if (ex.getRejectedValue() != null) {
            body.put("rejectedValue", ex.getRejectedValue());
        }
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general runtime exceptions and returns HTTP 500 Internal Server Error.
     * 
     * This is a fallback handler for unexpected exceptions that are not
     * specifically handled by other methods.
     * 
     * @param ex the exception that was thrown
     * @param request the web request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        Map<String, Object> body = createErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles general exceptions and returns HTTP 500 Internal Server Error.
     * 
     * This is the most general fallback handler for any exception not
     * caught by more specific handlers.
     * 
     * @param ex the exception that was thrown
     * @param request the web request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> body = createErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a consistent error response body structure.
     * 
     * Private helper method following the Single Responsibility Principle
     * and DRY principle by centralizing error response format.
     * 
     * @param status HTTP status code
     * @param error error type/category
     * @param message detailed error message
     * @param path request path where the error occurred
     * @return Map containing structured error information
     */
    private Map<String, Object> createErrorBody(int status, String error, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path.replace("uri=", ""));
        return body;
    }
}