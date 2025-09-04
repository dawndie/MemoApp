package memoapp.dto;

import memoapp.entity.Priority;

import java.util.Map;

public class PriorityStatistics {
    
    private Map<Priority, Long> priorityCounts;
    private long totalMemos;
    private Priority mostCommonPriority;
    
    public PriorityStatistics() {}
    
    public PriorityStatistics(Map<Priority, Long> priorityCounts, long totalMemos, Priority mostCommonPriority) {
        this.priorityCounts = priorityCounts;
        this.totalMemos = totalMemos;
        this.mostCommonPriority = mostCommonPriority;
    }
    
    public Map<Priority, Long> getPriorityCounts() {
        return priorityCounts;
    }
    
    public void setPriorityCounts(Map<Priority, Long> priorityCounts) {
        this.priorityCounts = priorityCounts;
    }
    
    public long getTotalMemos() {
        return totalMemos;
    }
    
    public void setTotalMemos(long totalMemos) {
        this.totalMemos = totalMemos;
    }
    
    public Priority getMostCommonPriority() {
        return mostCommonPriority;
    }
    
    public void setMostCommonPriority(Priority mostCommonPriority) {
        this.mostCommonPriority = mostCommonPriority;
    }
}