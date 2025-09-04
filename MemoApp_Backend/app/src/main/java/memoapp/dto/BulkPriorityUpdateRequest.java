package memoapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import memoapp.entity.Priority;

import java.util.List;

public class BulkPriorityUpdateRequest {
    
    @NotEmpty(message = "Memo IDs cannot be empty")
    @Size(max = 100, message = "Cannot update more than 100 memos at once")
    private List<Long> memoIds;
    
    @NotNull(message = "Priority is required")
    private Priority priority;
    
    public BulkPriorityUpdateRequest() {}
    
    public BulkPriorityUpdateRequest(List<Long> memoIds, Priority priority) {
        this.memoIds = memoIds;
        this.priority = priority;
    }
    
    public List<Long> getMemoIds() {
        return memoIds;
    }
    
    public void setMemoIds(List<Long> memoIds) {
        this.memoIds = memoIds;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}