package memoapp.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
    HIGH("HIGH", 3),
    MEDIUM("MEDIUM", 2), 
    LOW("LOW", 1),
    NONE("NONE", 0);
    
    private final String value;
    private final int order;
    
    Priority(String value, int order) {
        this.value = value;
        this.order = order;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public int getOrder() {
        return order;
    }
    
    @JsonCreator
    public static Priority fromValue(String value) {
        if (value == null) {
            return NONE;
        }
        
        for (Priority priority : Priority.values()) {
            if (priority.value.equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}