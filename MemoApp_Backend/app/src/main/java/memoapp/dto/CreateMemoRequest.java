package memoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import memoapp.entity.Priority;

/**
 * Data Transfer Object for creating a new memo.
 *
 * This DTO follows the Single Responsibility Principle by encapsulating
 * the data and validation rules required for memo creation.
 *
 * Separates the API contract from the internal entity model, allowing
 * independent evolution of the API and domain model (Open/Closed Principle).
 */
public class CreateMemoRequest {

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 10000, message = "Content cannot exceed 10,000 characters")
    private String content;

    private Priority priority;

    /**
     * Default constructor required for JSON deserialization.
     */
    public CreateMemoRequest() {}

    /**
     * Constructor with all fields for convenience in testing.
     *
     * @param title the memo title
     * @param content the memo content
     * @param priority the memo priority
     */
    public CreateMemoRequest(String title, String content, Priority priority) {
        this.title = title;
        this.content = content;
        this.priority = priority;
    }

    /**
     * Gets the memo title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the memo title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the memo content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the memo content.
     *
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the memo priority.
     *
     * @return the priority (may be null, will default to NONE)
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the memo priority.
     *
     * @param priority the priority to set
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "CreateMemoRequest{" +
                "title='" + title + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) : null) + "..." + '\'' +
                ", priority=" + priority +
                '}';
    }
}
