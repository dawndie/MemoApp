package memoapp.exception;

/**
 * Custom exception thrown when a memo is not found in the system.
 * 
 * This exception follows the Single Responsibility Principle by having a single,
 * well-defined purpose: representing the absence of a requested memo.
 * 
 * Extends RuntimeException to provide unchecked exception behavior,
 * which is appropriate for business logic violations that should be handled
 * by the application's exception handling infrastructure.
 */
public class MemoNotFoundException extends RuntimeException {
    
    private final Long memoId;
    
    /**
     * Constructs a new MemoNotFoundException with the specified memo ID.
     * 
     * @param memoId the ID of the memo that was not found
     */
    public MemoNotFoundException(Long memoId) {
        super(String.format("Memo with id %d not found", memoId));
        this.memoId = memoId;
    }
    
    /**
     * Constructs a new MemoNotFoundException with a custom message and memo ID.
     * 
     * @param message the custom error message
     * @param memoId the ID of the memo that was not found
     */
    public MemoNotFoundException(String message, Long memoId) {
        super(message);
        this.memoId = memoId;
    }
    
    /**
     * Constructs a new MemoNotFoundException with a custom message, memo ID, and cause.
     * 
     * @param message the custom error message
     * @param memoId the ID of the memo that was not found
     * @param cause the underlying cause of this exception
     */
    public MemoNotFoundException(String message, Long memoId, Throwable cause) {
        super(message, cause);
        this.memoId = memoId;
    }
    
    /**
     * Gets the ID of the memo that was not found.
     * 
     * @return the memo ID
     */
    public Long getMemoId() {
        return memoId;
    }
}