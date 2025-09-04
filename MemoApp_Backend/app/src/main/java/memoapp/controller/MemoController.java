package memoapp.controller;

import jakarta.validation.Valid;
import memoapp.dto.BulkPriorityUpdateRequest;
import memoapp.dto.PriorityStatistics;
import memoapp.dto.PriorityUpdateRequest;
import memoapp.entity.Memo;
import memoapp.entity.Priority;
import memoapp.service.MemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import java.util.List;

/**
 * REST controller for memo operations.
 * 
 * Updated to work with the refactored MemoService that follows SOLID principles.
 * The service now throws meaningful exceptions instead of returning null values,
 * so exception handling is managed by Spring's global exception handler.
 */
@RestController
@RequestMapping("/api/memos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MemoController {

    private final MemoService memoService;

    /**
     * Constructor injection following Dependency Inversion Principle.
     * Replaced @Autowired field injection with constructor injection for better testability.
     */
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @GetMapping
    public List<Memo> getAllMemos(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String sort) {
        
        // Handle priority filtering
        if (priority != null && !priority.isEmpty()) {
            List<Priority> priorities = Arrays.stream(priority.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Priority::fromValue)
                    .collect(java.util.stream.Collectors.toList());
            return memoService.getMemosByPriority(priorities);
        }
        
        // Handle sorting
        if (sort != null && !sort.isEmpty()) {
            return memoService.getMemosSortedByPriority(sort);
        }
        
        return memoService.getAllMemos();
    }

    /**
     * Simplified method that relies on service exception handling.
     * The service throws MemoNotFoundException instead of returning null,
     * which will be handled by Spring's exception handling mechanism.
     */
    @GetMapping("/{id}")
    public Memo getMemoById(@PathVariable Long id) {
        return memoService.getMemoById(id);
    }

    /**
     * Updated to use the new createMemo method name for better semantic clarity.
     */
    @PostMapping
    public Memo createMemo(@RequestBody Memo memo) {
        return memoService.createMemo(memo);
    }

    /**
     * Simplified update method that relies on service validation and exception handling.
     * The service handles existence checking and throws appropriate exceptions.
     */
    @PutMapping("/{id}")
    public Memo updateMemo(@PathVariable Long id, @RequestBody Memo memo) {
        return memoService.updateMemo(id, memo);
    }

    /**
     * Simplified delete method that relies on service validation and exception handling.
     * The service throws exceptions for non-existent memos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Updates the priority of a specific memo.
     * 
     * @param id memo ID to update
     * @param request priority update request
     * @return updated memo
     */
    @PutMapping("/{id}/priority")
    public Memo updateMemoPriority(@PathVariable Long id, @Valid @RequestBody PriorityUpdateRequest request) {
        return memoService.updateMemoPriority(id, request.getPriority());
    }
    
    /**
     * Bulk update priority for multiple memos.
     * 
     * @param request bulk priority update request
     * @return list of updated memos
     */
    @PostMapping("/bulk/priority")
    public List<Memo> bulkUpdatePriority(@Valid @RequestBody BulkPriorityUpdateRequest request) {
        return memoService.bulkUpdatePriority(request);
    }
    
    /**
     * Get priority statistics for all memos.
     * 
     * @return priority statistics
     */
    @GetMapping("/stats/priority")
    public PriorityStatistics getPriorityStatistics() {
        return memoService.getPriorityStatistics();
    }
}