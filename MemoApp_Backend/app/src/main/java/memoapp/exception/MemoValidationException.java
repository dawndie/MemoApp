package memoapp.exception;

/**
 * Custom exception thrown when memo validation fails.
 * 
 * This exception follows the Single Responsibility Principle by having a single,
 * well-defined purpose: representing validation errors for memo objects.
 * 
 * Extends RuntimeException to provide unchecked exception behavior,
 * which is appropriate for validation failures that should be handled
 * by the application's exception handling infrastructure.
 */
public class MemoValidationException extends RuntimeException {
    
    private final String fieldName;
    private final Object rejectedValue;
    
    /**
     * Constructs a new MemoValidationException with the specified message.
     * 
     * @param message the validation error message
     */
    public MemoValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.rejectedValue = null;
    }
    
    /**
     * Constructs a new MemoValidationException with field-specific details.
     * 
     * @param message the validation error message
     * @param fieldName the name of the field that failed validation
     * @param rejectedValue the value that was rejected
     */
    public MemoValidationException(String message, String fieldName, Object rejectedValue) {
        super(message);
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }
    
    /**
     * Constructs a new MemoValidationException with a cause.
     * 
     * @param message the validation error message
     * @param cause the underlying cause of this exception
     */
    public MemoValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.rejectedValue = null;
    }
    
    /**
     * Gets the name of the field that failed validation.
     * 
     * @return the field name, or null if not specified
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Gets the value that was rejected during validation.
     * 
     * @return the rejected value, or null if not specified
     */
    public Object getRejectedValue() {
        return rejectedValue;
    }
}