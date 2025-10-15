package memoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import memoapp.entity.Priority;

/**
 * Data Transfer Object for creating a new memo.
 *
 * This DTO follows best practices by:
 * - Separating API contracts from domain entities
 * - Using Bean Validation annotations for input validation
 * - Providing clear validation messages for better API usability
 * - Following the Single Responsibility Principle
 */
public class CreateMemoRequest {

    @NotBlank(message = "Title is required and cannot be empty")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required and cannot be empty")
    @Size(max = 10000, message = "Content cannot exceed 10,000 characters")
    private String content;

    @NotNull(message = "Priority is required")
    private Priority priority;

    /**
     * Default constructor for Jackson deserialization.
     */
    public CreateMemoRequest() {}

    /**
     * Constructor with all fields for testing and convenience.
     *
     * @param title the memo title
     * @param content the memo content
     * @param priority the memo priority level
     */
    public CreateMemoRequest(String title, String content, Priority priority) {
        this.title = title;
        this.content = content;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "CreateMemoRequest{" +
                "title='" + title + '\'' +
                ", content='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", priority=" + priority +
                '}';
    }
}
