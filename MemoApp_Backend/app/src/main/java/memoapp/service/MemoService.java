package memoapp.service;

import memoapp.dto.BulkPriorityUpdateRequest;
import memoapp.dto.PriorityStatistics;
import memoapp.entity.Memo;
import memoapp.entity.Priority;
import memoapp.exception.MemoNotFoundException;
import memoapp.exception.MemoValidationException;
import memoapp.repository.MemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing memo operations.
 * 
 * This service follows SOLID principles:
 * - Single Responsibility: Handles only memo-related business logic
 * - Open/Closed: Can be extended through interfaces without modification
 * - Liskov Substitution: Can be replaced by any implementation of memo service interface
 * - Interface Segregation: Provides focused methods for specific memo operations
 * - Dependency Inversion: Depends on MemoRepository abstraction, not concrete implementation
 * 
 * Uses constructor injection for better testability and immutability.
 * Includes proper transaction management and input validation.
 * Throws meaningful exceptions with detailed error information.
 */
@Service
@Transactional(readOnly = true)
public class MemoService {
    
    private final MemoRepository memoRepository;
    
    /**
     * Constructor injection following Dependency Inversion Principle.
     * Makes dependencies explicit and enables better testing.
     * 
     * @param memoRepository the repository for memo data access operations
     */
    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }
    
    /**
     * Retrieves all memos from the system.
     * 
     * This method follows the Single Responsibility Principle by having
     * a single, well-defined purpose: fetching all memos.
     * 
     * @return List of all memos, never null (may be empty)
     */
    public List<Memo> getAllMemos() {
        return memoRepository.findAll();
    }
    
    /**
     * Retrieves a specific memo by its unique identifier.
     * 
     * Demonstrates proper exception handling and null object pattern avoidance.
     * Throws a meaningful exception instead of returning null, following
     * the principle of "fail fast" and providing clear error information.
     * 
     * @param id the unique identifier of the memo to retrieve
     * @return the memo with the specified ID
     * @throws MemoNotFoundException if no memo exists with the given ID
     * @throws MemoValidationException if the ID is null or invalid
     */
    public Memo getMemoById(Long id) {
        validateMemoId(id);
        
        return memoRepository.findById(id)
                .orElseThrow(() -> new MemoNotFoundException(id));
    }
    
    /**
     * Creates a new memo in the system.
     * 
     * Includes comprehensive validation and follows the principle of
     * defensive programming by validating all inputs before processing.
     * 
     * @param memo the memo to create
     * @return the created memo with generated ID and timestamps
     * @throws MemoValidationException if the memo is invalid
     */
    @Transactional
    public Memo createMemo(Memo memo) {
        validateMemoForCreation(memo);
        
        // Clear the ID to ensure it's treated as a new entity
        // This follows defensive programming principles
        if (memo.getId() != null) {
            memo.setId(null);
        }
        
        return memoRepository.save(memo);
    }
    
    /**
     * Updates an existing memo in the system.
     * 
     * Ensures the memo exists before attempting to update it,
     * following the principle of failing fast with meaningful errors.
     * 
     * @param id the ID of the memo to update
     * @param updatedMemo the memo data to update with
     * @return the updated memo
     * @throws MemoNotFoundException if no memo exists with the given ID
     * @throws MemoValidationException if the update data is invalid
     */
    @Transactional
    public Memo updateMemo(Long id, Memo updatedMemo) {
        validateMemoId(id);
        validateMemoForUpdate(updatedMemo);
        
        // Verify the memo exists before updating
        Memo existingMemo = getMemoById(id);
        
        // Update the fields while preserving the original ID and created timestamp
        existingMemo.setTitle(updatedMemo.getTitle());
        existingMemo.setContent(updatedMemo.getContent());
        existingMemo.setPriority(updatedMemo.getPriority());
        
        return memoRepository.save(existingMemo);
    }
    
    /**
     * Deletes a memo from the system.
     * 
     * Verifies the memo exists before deletion to provide clear
     * feedback and prevent silent failures.
     * 
     * @param id the ID of the memo to delete
     * @throws MemoNotFoundException if no memo exists with the given ID
     * @throws MemoValidationException if the ID is null or invalid
     */
    @Transactional
    public void deleteMemo(Long id) {
        validateMemoId(id);
        
        // Verify the memo exists before attempting deletion
        // This provides better error handling and user feedback
        if (!memoRepository.existsById(id)) {
            throw new MemoNotFoundException(id);
        }
        
        memoRepository.deleteById(id);
    }
    
    /**
     * Checks if a memo exists with the given ID.
     * 
     * Utility method following the Interface Segregation Principle
     * by providing a focused operation for existence checking.
     * 
     * @param id the ID to check
     * @return true if a memo exists with the given ID, false otherwise
     * @throws MemoValidationException if the ID is null or invalid
     */
    public boolean memoExists(Long id) {
        validateMemoId(id);
        return memoRepository.existsById(id);
    }
    
    /**
     * Retrieves all memos filtered by priority levels.
     * 
     * @param priorities list of priorities to filter by
     * @return list of memos with specified priorities
     * @throws MemoValidationException if priorities list is invalid
     */
    public List<Memo> getMemosByPriority(List<Priority> priorities) {
        if (priorities == null || priorities.isEmpty()) {
            return getAllMemos();
        }
        
        // Remove null values and duplicates
        List<Priority> validPriorities = priorities.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        if (validPriorities.isEmpty()) {
            throw new MemoValidationException("At least one valid priority must be specified");
        }
        
        return memoRepository.findByPrioritiesOrderByPriorityDescCreatedAtDesc(validPriorities);
    }
    
    /**
     * Retrieves all memos sorted by priority.
     * 
     * @param sortOrder "priority_desc" or "priority_asc"
     * @return list of memos sorted by priority
     */
    public List<Memo> getMemosSortedByPriority(String sortOrder) {
        if (sortOrder == null || sortOrder.isEmpty()) {
            return getAllMemos();
        }
        
        switch (sortOrder.toLowerCase()) {
            case "priority_desc":
                return memoRepository.findAllOrderByPriorityDescCreatedAtDesc();
            case "priority_asc":
                return memoRepository.findAllOrderByPriorityAscCreatedAtDesc();
            default:
                throw new MemoValidationException("Invalid sort order. Use 'priority_desc' or 'priority_asc'", "sort", sortOrder);
        }
    }
    
    /**
     * Updates the priority of a specific memo.
     * 
     * @param id memo ID to update
     * @param priority new priority value
     * @return updated memo
     * @throws MemoNotFoundException if memo doesn't exist
     * @throws MemoValidationException if priority is invalid
     */
    @Transactional
    public Memo updateMemoPriority(Long id, Priority priority) {
        validateMemoId(id);
        if (priority == null) {
            throw new MemoValidationException("Priority cannot be null", "priority", null);
        }
        
        Memo existingMemo = getMemoById(id);
        existingMemo.setPriority(priority);
        
        return memoRepository.save(existingMemo);
    }
    
    /**
     * Updates the priority of multiple memos in bulk.
     * 
     * @param request bulk update request containing memo IDs and new priority
     * @return list of updated memos
     * @throws MemoValidationException if request is invalid
     * @throws MemoNotFoundException if any memo doesn't exist
     */
    @Transactional
    public List<Memo> bulkUpdatePriority(BulkPriorityUpdateRequest request) {
        if (request == null) {
            throw new MemoValidationException("Bulk update request cannot be null");
        }
        
        List<Long> memoIds = request.getMemoIds();
        Priority priority = request.getPriority();
        
        if (memoIds == null || memoIds.isEmpty()) {
            throw new MemoValidationException("Memo IDs cannot be empty");
        }
        
        if (memoIds.size() > 100) {
            throw new MemoValidationException("Cannot update more than 100 memos at once");
        }
        
        if (priority == null) {
            throw new MemoValidationException("Priority cannot be null", "priority", null);
        }
        
        // Validate all memo IDs and check existence
        for (Long id : memoIds) {
            validateMemoId(id);
            if (!memoRepository.existsById(id)) {
                throw new MemoNotFoundException(id);
            }
        }
        
        // Fetch all memos and update their priority
        List<Memo> memos = memoRepository.findAllById(memoIds);
        for (Memo memo : memos) {
            memo.setPriority(priority);
        }
        
        return memoRepository.saveAll(memos);
    }
    
    /**
     * Retrieves priority statistics for all memos.
     * 
     * @return statistics object containing priority counts and analysis
     */
    public PriorityStatistics getPriorityStatistics() {
        Map<Priority, Long> priorityCounts = new HashMap<>();
        
        // Count memos for each priority level
        for (Priority priority : Priority.values()) {
            long count = memoRepository.countByPriority(priority);
            priorityCounts.put(priority, count);
        }
        
        long totalMemos = memoRepository.count();
        
        // Find most common priority
        Priority mostCommonPriority = priorityCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Priority.NONE);
        
        return new PriorityStatistics(priorityCounts, totalMemos, mostCommonPriority);
    }
    
    /**
     * Validates a memo ID for null and negative values.
     * 
     * Private helper method following the Single Responsibility Principle
     * and DRY (Don't Repeat Yourself) principle.
     * 
     * @param id the ID to validate
     * @throws MemoValidationException if the ID is invalid
     */
    private void validateMemoId(Long id) {
        if (id == null) {
            throw new MemoValidationException("Memo ID cannot be null", "id", id);
        }
        if (id <= 0) {
            throw new MemoValidationException("Memo ID must be a positive number", "id", id);
        }
    }
    
    /**
     * Validates a memo object for creation operations.
     * 
     * Comprehensive validation following defensive programming principles.
     * Separated from update validation to follow Single Responsibility Principle.
     * 
     * @param memo the memo to validate
     * @throws MemoValidationException if the memo is invalid
     */
    private void validateMemoForCreation(Memo memo) {
        if (memo == null) {
            throw new MemoValidationException("Memo cannot be null");
        }
        
        validateMemoTitle(memo.getTitle());
        validateMemoContent(memo.getContent());
    }
    
    /**
     * Validates a memo object for update operations.
     * 
     * Similar to creation validation but may have different rules in the future.
     * Follows Open/Closed Principle by allowing extension without modification.
     * 
     * @param memo the memo to validate
     * @throws MemoValidationException if the memo is invalid
     */
    private void validateMemoForUpdate(Memo memo) {
        if (memo == null) {
            throw new MemoValidationException("Memo cannot be null");
        }
        
        validateMemoTitle(memo.getTitle());
        validateMemoContent(memo.getContent());
    }
    
    /**
     * Validates memo title field.
     * 
     * Focused validation method following Single Responsibility Principle.
     * 
     * @param title the title to validate
     * @throws MemoValidationException if the title is invalid
     */
    private void validateMemoTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new MemoValidationException("Memo title cannot be null or empty", "title", title);
        }
        
        if (title.trim().length() > 255) {
            throw new MemoValidationException("Memo title cannot exceed 255 characters", "title", title);
        }
    }
    
    /**
     * Validates memo content field.
     * 
     * Focused validation method following Single Responsibility Principle.
     * Content can be null or empty, but if present, should not exceed reasonable limits.
     * 
     * @param content the content to validate
     * @throws MemoValidationException if the content is invalid
     */
    private void validateMemoContent(String content) {
        if (content != null && content.length() > 10000) {
            throw new MemoValidationException("Memo content cannot exceed 10,000 characters", "content", content);
        }
    }
}