package memoapp.dto;

import memoapp.entity.Memo;
import memoapp.entity.Priority;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for memo API responses.
 *
 * This DTO follows best practices by:
 * - Separating API responses from domain entities
 * - Providing a stable API contract that can evolve independently from the entity
 * - Exposing only the necessary data to API consumers
 * - Following the Single Responsibility Principle
 *
 * This prevents accidental exposure of sensitive data and allows for
 * flexible API versioning without affecting the domain model.
 */
public class MemoResponse {

    private Long id;
    private String title;
    private String content;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor for Jackson serialization.
     */
    public MemoResponse() {}

    /**
     * Constructor with all fields.
     *
     * @param id the memo ID
     * @param title the memo title
     * @param content the memo content
     * @param priority the memo priority level
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public MemoResponse(Long id, String title, String content, Priority priority,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to create a MemoResponse from a Memo entity.
     *
     * This follows the Factory Pattern and provides a clean way to convert
     * domain entities to DTOs, centralizing the mapping logic.
     *
     * @param memo the memo entity to convert
     * @return a new MemoResponse instance
     */
    public static MemoResponse fromEntity(Memo memo) {
        if (memo == null) {
            return null;
        }

        return new MemoResponse(
            memo.getId(),
            memo.getTitle(),
            memo.getContent(),
            memo.getPriority(),
            memo.getCreatedAt(),
            memo.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MemoResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
